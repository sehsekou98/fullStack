package com.sekou.fullstack.controller;

import com.sekou.fullstack.common.PageResponse;
import com.sekou.fullstack.module.book.BookRequest;
import com.sekou.fullstack.module.book.BookResponse;
import com.sekou.fullstack.service.BookService;
import com.sekou.fullstack.service.BorrowedBookResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("book")
@RequiredArgsConstructor
@Tag(name = "Book")
public class BookController {
    private final BookService service;

    @PostMapping
    public ResponseEntity<Integer> saveBook(
            @Valid @RequestBody BookRequest request,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.saveBook(request, connectedUser));
    }

    @GetMapping("{book_id}")
    public ResponseEntity<BookResponse> findBookById(
            @PathVariable("book_id") Integer bookId){
        return ResponseEntity.ok(service.findBookById(bookId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<BookResponse>> findAllBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "0", required = false) int size,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.findAllBooks(page, size, connectedUser));
    }

    @GetMapping("/owner")
    public ResponseEntity<PageResponse<BookResponse>> findBooksByOwner(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "", required = false) int size,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.findAllBooksByOwner(page, size, connectedUser));
    }

    @GetMapping("/borrowed")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllBorrowedBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.findAllBorrowedBooks(page, size, connectedUser));
    }

    @GetMapping("/returned")
    public ResponseEntity<PageResponse<BorrowedBookResponse>>findAllReturnedBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "", required = false) int size,
            Authentication connectedUse
    ){
        return ResponseEntity.ok(service.findAllReturnedBooks(page, size, connectedUse));
    }

    @PatchMapping("/shareable/{book_id}")
    public ResponseEntity<Integer> updateShareableStatus(
            @PathVariable("book_id") Integer bookId,
            Authentication connectedUse
    ){
        return ResponseEntity.ok(service.updateShareableStatus(bookId, connectedUse));
    }

    @PatchMapping("/archived/{book_id}")
    public ResponseEntity<Integer> updateArchivedStatus(
            @PathVariable("book_id") Integer bookId,
            Authentication connectedUse
    ){
        return ResponseEntity.ok(service.updateArchivedStatus(bookId, connectedUse));
    }

    @PostMapping("/borrow/{book_id}")
    public ResponseEntity<Integer> borrowBook(
            @PathVariable("book_id") Integer bookId,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.BorrowBook(bookId, connectedUser));
    }


    @PatchMapping("/borrow/return/{book_id}")
    public ResponseEntity<Integer> returnBorrowBook(
            @PathVariable("book_id") Integer bookId,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.returnBorrowBook(bookId, connectedUser));
    }
    @PatchMapping("/borrow/return/approve/{book_id}")
    public ResponseEntity<Integer> approveReturnBook(
            @PathVariable("book_id") Integer bookId,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.approveReturnBorrowBook(bookId, connectedUser));
    }
}
