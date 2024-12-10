package org.example.product.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.product.dto.Request.CreateReviewRequest;
import org.example.product.dto.Response.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

public interface ReviewService {
    ResponseEntity<Page<ReviewResponse>> getReviews(String productId, int page, int size, boolean ascending);

    ResponseEntity<ReviewResponse> getReview(String productId);

    ResponseEntity<ReviewResponse> createReview(String productId, CreateReviewRequest createReviewRequest, HttpServletRequest request);

    ResponseEntity<ReviewResponse> updateReview(String reviewId, CreateReviewRequest createReviewRequest, HttpServletRequest request);

    ResponseEntity<String> deleteReview(String reviewId, HttpServletRequest request);
}
