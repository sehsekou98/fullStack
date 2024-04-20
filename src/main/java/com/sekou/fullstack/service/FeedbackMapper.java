package com.sekou.fullstack.service;

import com.sekou.fullstack.module.book.Book;
import com.sekou.fullstack.module.feedback.FeedBack;
import com.sekou.fullstack.module.feedback.FeedbackRequest;
import org.springframework.stereotype.Service;

@Service
public class FeedbackMapper {
    public FeedBack toFeedback(FeedbackRequest request) {
        return FeedBack.builder()
                .note(request.note())
                .comment(request.comment())
                .book(Book.builder()
                        .id(request.bookId())
                        .archived(false)
                        .shareable(false)
                        .build()
                )
        .build();
    }
}
