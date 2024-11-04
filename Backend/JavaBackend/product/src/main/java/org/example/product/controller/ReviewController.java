package org.example.product.controller;

import lombok.RequiredArgsConstructor;
import org.example.product.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/review")
public class ReviewController {
    private final ReviewService reviewService;

    @RequestMapping(path = "/{productId}")
    public ResponseEntity<?> getReviews(@PathVariable String productId) {
        return reviewService.getReviews(productId);
    }
}
