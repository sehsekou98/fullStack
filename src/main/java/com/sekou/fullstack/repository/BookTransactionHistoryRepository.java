package com.sekou.fullstack.repository;

import com.sekou.fullstack.module.BookTransactionHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistoryRepository, Integer> {

    @Query("""
SELECT history
FROM BookTransactionHistory history
WHERE history.user.id = :userId
""")

    Page<BookTransactionHistory> findAllBorrowedBooks(Pageable pageable, Integer userId);
}
