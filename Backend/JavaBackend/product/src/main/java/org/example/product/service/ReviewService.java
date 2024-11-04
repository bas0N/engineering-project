package org.example.product.service;

import org.springframework.http.ResponseEntity;

public interface ReviewService {
    ResponseEntity<?> getReviews(String productId);

    ResponseEntity<?> getReview(String productId);
}
