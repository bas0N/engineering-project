package org.example.product.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.product.dto.Request.CreateReviewRequest;
import org.springframework.http.ResponseEntity;

public interface ReviewService {
    ResponseEntity<?> getReviews(String productId);

    ResponseEntity<?> getReview(String productId);

    ResponseEntity<?> createReview(String productId, CreateReviewRequest createReviewRequest, HttpServletRequest request);

    ResponseEntity<?> updateReview(String reviewId, CreateReviewRequest createReviewRequest, HttpServletRequest request);
}
