package org.example.product.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.product.dto.Request.CreateReviewRequest;
import org.example.product.dto.Response.ReviewResponse;
import org.example.product.entity.Review;
import org.example.product.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product/review")
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping(path = "/{productId}/reviews")
    public ResponseEntity<Page<ReviewResponse>> getReviews(@PathVariable String productId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "true") boolean ascending) {
        return reviewService.getReviews(productId, page, size, ascending);
    }

    @GetMapping(path = "/{reviewId}")
    public ResponseEntity<ReviewResponse> getReview(@PathVariable String reviewId) {
        return reviewService.getReview(reviewId);
    }

    @PostMapping(path = "/{productId}")
    public ResponseEntity<ReviewResponse> createReview(@PathVariable String productId, @RequestBody @Valid CreateReviewRequest createReviewRequest, HttpServletRequest request) {
        return reviewService.createReview(productId, createReviewRequest, request);
    }

    @PutMapping(path = "/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(@PathVariable String reviewId, @RequestBody CreateReviewRequest createReviewRequest, HttpServletRequest request) {
        return reviewService.updateReview(reviewId, createReviewRequest, request);
    }

    @DeleteMapping(path = "/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable String reviewId) {
        return reviewService.deleteReview(reviewId);
    }


}
