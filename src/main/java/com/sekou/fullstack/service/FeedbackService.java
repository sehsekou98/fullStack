package com.sekou.fullstack.service;

import com.sekou.fullstack.common.PageResponse;
import com.sekou.fullstack.exception.OperationNotPermittedException;
import com.sekou.fullstack.module.book.Book;
import com.sekou.fullstack.module.feedback.FeedBack;
import com.sekou.fullstack.module.feedback.FeedbackRequest;
import com.sekou.fullstack.module.feedback.FeedbackResponse;
import com.sekou.fullstack.repository.BookRepository;
import com.sekou.fullstack.repository.FeedbackRepository;
import com.sekou.fullstack.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public PageResponse<FeedbackResponse> findAllFeedbackByBook(Integer bookId, int page, int size, Authentication connectedUser) {
        Pageable pageable = PageRequest.of(page, size);
        User user = ((User) connectedUser.getPrincipal());
        Page<FeedBack> feedBacks = feedbackRepository.findAllByBookId(bookId, pageable);
        List<FeedbackResponse> feedbackResponses = feedBacks.stream()
                .map(f -> feedbackMapper.tofeedbackResponse(f,user.getId()))
                .toList();
        return new PageResponse<>(
              feedbackResponses,
              feedBacks.getNumber(),
              feedBacks.getSize(),
              feedBacks.getTotalElements(),
              feedBacks.getTotalPages(),
              feedBacks.isFirst(),
              feedBacks.isLast()
        );
    }
}
