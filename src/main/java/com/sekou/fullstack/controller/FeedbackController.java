package com.sekou.fullstack.controller;

import com.sekou.fullstack.common.PageResponse;
import com.sekou.fullstack.module.feedback.FeedbackRequest;
import com.sekou.fullstack.module.feedback.FeedbackResponse;
import com.sekou.fullstack.service.FeedbackService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("feedback")
@Tag(name = "Feedback")
public class FeedbackController {

    private final FeedbackService service;

    @PostMapping
    public ResponseEntity<Integer> saveFeedback(
            @Valid @RequestBody FeedbackRequest request,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.saveFeedback(request, connectedUser));
    }

    @GetMapping("/book/{book-id}")
    public ResponseEntity<PageResponse<FeedbackResponse>> findAllFeedbackByBook(
        @PathVariable("book-id") Integer bookId,
        @RequestParam(name = "page", defaultValue = "o", required = false) int page,
        @RequestParam(name = "size", defaultValue = "10", required = false) int size,
        Authentication connectedUser
    ){
        return ResponseEntity.ok(service.findAllFeedbackByBook(bookId, page, size, connectedUser));
    }
}
