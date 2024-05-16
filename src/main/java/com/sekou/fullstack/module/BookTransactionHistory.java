package com.sekou.fullstack.module;

import com.sekou.fullstack.common.CommonEntity;
import com.sekou.fullstack.module.book.Book;
import com.sekou.fullstack.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class BookTransactionHistory extends CommonEntity {
   // @ManyToOne
  //  @JoinColumn(name="user_id")
   // private User user;
   @Column(name="user_id")
    private String userId;

    @ManyToOne
    @JoinColumn(name="book_id")
    private Book book;
    private boolean returned;
    private boolean returnApproved;
}
