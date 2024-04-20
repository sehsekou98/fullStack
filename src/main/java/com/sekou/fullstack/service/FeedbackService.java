package com.sekou.fullstack.service;

import com.sekou.fullstack.exception.OperationNotPermittedException;
import com.sekou.fullstack.module.book.Book;
import com.sekou.fullstack.module.feedback.FeedBack;
import com.sekou.fullstack.module.feedback.FeedbackRequest;
import com.sekou.fullstack.repository.BookRepository;
import com.sekou.fullstack.repository.FeedbackRepository;
import com.sekou.fullstack.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
public class FeedbackService {
    private final BookRepository bookRepository;
    private final FeedbackMapper feedbackMapper;
    private final FeedbackRepository feedbackRepository;


    public Integer saveFeedback(FeedbackRequest request, Authentication connectedUser) {
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new EntityNotFoundException("No book with ID:: " + request.bookId()));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("You an not give a feedback for an archived or not shareable book.");
        }
        User user = ((User) connectedUser.getPrincipal());
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You can not give a feedback to your own book.");
        }
        FeedBack feedback = feedbackMapper.toFeedback(request);
        return feedbackRepository.save(feedback).getId();
    }
}
