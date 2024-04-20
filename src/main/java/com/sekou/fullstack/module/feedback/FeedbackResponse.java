package com.sekou.fullstack.module.feedback;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackResponse {
    private Double note;
    private String comment;
    private boolean ownFeedBack;
}
