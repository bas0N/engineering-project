package org.example.product.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.product.dto.Request.CreateReviewRequest;
import org.example.product.service.ReviewService;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/review")
public class ReviewController {
    private final ReviewService reviewService;

    @RequestMapping(path = "/{productId}", method = RequestMethod.GET)
    public ResponseEntity<?> getReviews(@PathVariable String productId) {
        return reviewService.getReviews(productId);
    }

    @RequestMapping(path = "/{reviewId}", method = RequestMethod.GET)
    public ResponseEntity<?> getReview(@PathVariable String reviewId) {
        return reviewService.getReview(reviewId);
    }

    @RequestMapping(path = "/{productId}", method = RequestMethod.POST)
    public ResponseEntity<?> createReview(@PathVariable String productId, @RequestBody CreateReviewRequest createReviewRequest, HttpServletRequest request) {
        return reviewService.createReview(productId, createReviewRequest, request);
    }

    @RequestMapping(path = "/{reviewId}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateReview(@PathVariable String reviewId, @RequestBody CreateReviewRequest createReviewRequest, HttpServletRequest request) {
        return reviewService.updateReview(reviewId, createReviewRequest, request);
    }


}
