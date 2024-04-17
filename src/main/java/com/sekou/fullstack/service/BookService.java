package com.sekou.fullstack.service;

import com.sekou.fullstack.module.BookTransactionHistory;
import com.sekou.fullstack.module.book.Book;
import com.sekou.fullstack.module.book.BookRequest;
import com.sekou.fullstack.module.book.BookResponse;
import com.sekou.fullstack.repository.BookRepository;
import com.sekou.fullstack.repository.BookTransactionHistoryRepository;
import com.sekou.fullstack.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

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
}
