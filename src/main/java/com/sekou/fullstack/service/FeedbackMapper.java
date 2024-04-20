package com.sekou.fullstack.service;

import com.sekou.fullstack.module.book.Book;
import com.sekou.fullstack.module.feedback.FeedBack;
import com.sekou.fullstack.module.feedback.FeedbackRequest;
import com.sekou.fullstack.module.feedback.FeedbackResponse;
import org.springframework.stereotype.Service;

import java.util.Objects;

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

    public FeedbackResponse tofeedbackResponse(FeedBack f, Integer id) {
        return FeedbackResponse.builder()
                .note(f.getNote())
                .comment(f.getComment())
                .ownFeedBack(Objects.equals(f.getCreatedBy(), id))
                .build();
    }
}
