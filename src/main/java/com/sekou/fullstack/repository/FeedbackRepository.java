package com.sekou.fullstack.repository;

import com.sekou.fullstack.module.feedback.FeedBack;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<FeedBack, Integer> {
}
