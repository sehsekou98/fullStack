package com.sekou.fullstack.repository;

import com.sekou.fullstack.module.book.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Integer> {
}
