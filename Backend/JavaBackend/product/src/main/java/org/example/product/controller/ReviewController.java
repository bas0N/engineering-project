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

    @RequestMapping(path = "/{productId}/reviews", method = RequestMethod.GET)
    public ResponseEntity<Page<ReviewResponse>> getReviews(@PathVariable String productId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {   // domyślnie 10 elementów na stronę
        Page<ReviewResponse> reviews = reviewService.getReviews(productId, page, size);
        return ResponseEntity.ok(reviews);
    }

    @RequestMapping(path = "/{reviewId}", method = RequestMethod.GET)
    public ResponseEntity<ReviewResponse> getReview(@PathVariable String reviewId) {
        ReviewResponse response = reviewService.getReview(reviewId);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(path = "/{productId}", method = RequestMethod.POST)
    public ResponseEntity<ReviewResponse> createReview(@PathVariable String productId, @RequestBody @Valid CreateReviewRequest createReviewRequest, HttpServletRequest request) {
        ReviewResponse response = reviewService.createReview(productId, createReviewRequest, request);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(path = "/{reviewId}", method = RequestMethod.PUT)
    public ResponseEntity<ReviewResponse> updateReview(@PathVariable String reviewId, @RequestBody CreateReviewRequest createReviewRequest, HttpServletRequest request) {
        ReviewResponse response = reviewService.updateReview(reviewId, createReviewRequest, request);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(path = "/{reviewId}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteReview(@PathVariable String reviewId) {
        Boolean response =  reviewService.deleteReview(reviewId);
        return ResponseEntity.ok(response);
    }


}
