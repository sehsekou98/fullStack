package com.sekou.fullstack.service;

import com.sekou.fullstack.common.PageResponse;
import com.sekou.fullstack.exception.OperationNotPermittedException;
import com.sekou.fullstack.file.FileStorageService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

import static com.sekou.fullstack.module.book.BookSpecification.withOwnerId;

@Service
@RequiredArgsConstructor

public class BookService {
    private final BookMapper bookMapper;
    private final BookRepository bookRepository;
    private final FileStorageService fileStorageService;
    private final BookTransactionHistoryRepository bookTransactionHistoryRepository;



    public Integer saveBook(BookRequest request, Authentication connectedUser) {
      //  User user = ((User) connectedUser.getPrincipal());
        Book book = bookMapper.toBook(request);
       // book.setOwner(user);
        return bookRepository.save(book).getId();

    }

    public BookResponse findBookById(Integer bookId) {
        return bookRepository.findById(bookId)
                .map(bookMapper::toBookResponse)
                .orElseThrow(()-> new EntityNotFoundException("No book with the ID " + bookId));
    }

    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {
       // User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, connectedUser.getName());
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
      //  User user = ((User)  connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size,Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAll(withOwnerId(connectedUser.getName()), pageable);
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
        Page<BookTransactionHistory> allBorrowBooks = bookTransactionHistoryRepository.findAllBorrowedBooks(pageable, connectedUser.getName());
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

    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication connectedUse) {
        User user = ((User) connectedUse.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allReturnedBooks = bookTransactionHistoryRepository.findAllReturnedBooks(pageable, user.getId());
        List<BorrowedBookResponse> bookResponse = allReturnedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponse,
                allReturnedBooks.getNumber(),
                allReturnedBooks.getSize(),
                allReturnedBooks.getTotalElements(),
                allReturnedBooks.getTotalPages(),
                allReturnedBooks.isFirst(),
                allReturnedBooks.isLast()
        );
    }


    public Integer updateShareableStatus(Integer bookId, Authentication connectedUse) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
       // User user = ((User) connectedUse.getPrincipal());
        if (!Objects.equals(book.getCreatedBy(),connectedUse.getName())) {
            throw new OperationNotPermittedException("You are not allow to update this book shareable status.");

        }
        book.setShareable(!book.isShareable());
        bookRepository.save(book);
        return bookId;
    }

    public Integer updateArchivedStatus(Integer bookId, Authentication connectedUse) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
       // User user = ((User) connectedUse.getPrincipal());
        if (!Objects.equals(book.getCreatedBy(),connectedUse.getName())) {
            throw new OperationNotPermittedException("You are not allow to update this book archived status.");

        }
        book.setArchived(!book.isArchived());
        bookRepository.save(book);
        return bookId;
    }

    public Integer BorrowBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with the ID:: " + bookId));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("The requested book can not be borrow.");

        }
       // User user = ((User) connectedUser.getPrincipal());
        if (Objects.equals(book.getCreatedBy(),connectedUser.getName())) {
            throw new OperationNotPermittedException("You can not borrow your own book.");
        }
        final boolean isAlreadyBorrowed = bookTransactionHistoryRepository.isAlreadyBorrowedByUser(bookId, connectedUser.getName());
        if (isAlreadyBorrowed){
            throw new OperationNotPermittedException("The requested book is already borrowed.");
        }
        BookTransactionHistory bookTransactionHistory = BookTransactionHistory.builder()
                .userId(connectedUser.getName())
                .book(book)
                .returned(false)
                .returnApproved(false)
                .build();
        return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public Integer returnBorrowBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found."));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("The request book can not be borrow.");
        }
      //  User user = ((User) connectedUser.getPrincipal());
        if (Objects.equals(book.getCreatedBy(),connectedUser.getName())) {
            throw new OperationNotPermittedException("You can not borrow or return your own book.");
        }
        BookTransactionHistory bookTransactionHistory = bookTransactionHistoryRepository.findByBookIdAndUserId(bookId, connectedUser.getName())
                .orElseThrow(() -> new OperationNotPermittedException("You did not borrow this book."));
        bookTransactionHistory.setReturned(true);
        return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public Integer approveReturnBorrowBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));
        if (book.isArchived()|| !book.isShareable()) {
        throw new OperationNotPermittedException("The requested book cannot be borrow.");
    }
   // User user = ((User) connectedUser.getPrincipal());
        if (!Objects.equals(book.getCreatedBy(),connectedUser.getName())) {
        throw new OperationNotPermittedException("You can not borrow or return your own book.");
        }
        BookTransactionHistory bookTransactionHistory = bookTransactionHistoryRepository.findByBookIdAndOwnerId(bookId, connectedUser.getName())
                .orElseThrow(() -> new OperationNotPermittedException("You have not return this book."));
        bookTransactionHistory.setReturnApproved(true);
        return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public void uploadCoverPicture(MultipartFile file, Authentication connectedUser, Integer bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book with the ID:: " + bookId));
       // User user = ((User) connectedUser.getPrincipal());
        var bookCover = fileStorageService.saveFile(file, connectedUser.getName());
        book.setBookCover(bookCover);
        bookRepository.save(book);
    }
}
