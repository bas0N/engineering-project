package org.example.product.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.product.dto.Request.CreateReviewRequest;
import org.example.product.dto.Response.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

public interface ReviewService {
    Page<ReviewResponse> getReviews(String productId, int page, int size);

    ReviewResponse getReview(String productId);

    ReviewResponse createReview(String productId, CreateReviewRequest createReviewRequest, HttpServletRequest request);

    ReviewResponse updateReview(String reviewId, CreateReviewRequest createReviewRequest, HttpServletRequest request);

    Boolean deleteReview(String reviewId);
}
