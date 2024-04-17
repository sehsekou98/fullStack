package com.sekou.fullstack.service;

import com.sekou.fullstack.common.PageResponse;
import com.sekou.fullstack.module.BookTransactionHistory;
import com.sekou.fullstack.module.book.Book;
import com.sekou.fullstack.module.book.BookRequest;
import com.sekou.fullstack.module.book.BookResponse;
import com.sekou.fullstack.repository.BookRepository;
import com.sekou.fullstack.repository.BookTransactionHistoryRepository;
import com.sekou.fullstack.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.sekou.fullstack.module.book.BookSpecification.withOwnerId;

@Service
@RequiredArgsConstructor
public class BookService {
    private BookMapper bookMapper;
    private final BookRepository bookRepository;
    private final BookTransactionHistoryRepository bookTransactionHistoryRepository;



    public Integer saveBook(BookRequest request, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Book book = bookMapper.toBook(request);
        book.setOwner(user);
        return bookRepository.save(book).getId();

    }

    public BookResponse findBookById(Integer bookId) {
        return bookRepository.findById(bookId)
                .map(bookMapper::toBookResponse)
                .orElseThrow(()-> new EntityNotFoundException("No book with the ID " + bookId));
    }

    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, user.getId());
        List<BookResponse> booksResponse = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new PageResponse<>(
                booksResponse,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );

    }

    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
        User user = ((User)  connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size,Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAll(withOwnerId(user.getId()), pageable);
        List<BookResponse> bookResponse = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponse,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowBooks = bookTransactionHistoryRepository.findAllBorrowedBooks(pageable, user.getId());
        List<BorrowedBookResponse> bookResponse = allBorrowBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponse,
                allBorrowBooks.getNumber(),
                allBorrowBooks.getSize(),
                allBorrowBooks.getTotalElements(),
                allBorrowBooks.getTotalPages(),
                allBorrowBooks.isFirst(),
                allBorrowBooks.isLast()
        );
    }
}
