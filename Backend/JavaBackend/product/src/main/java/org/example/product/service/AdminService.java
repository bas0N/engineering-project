package org.example.product.service;

import org.springframework.http.ResponseEntity;

public interface AdminService {
    ResponseEntity<String> deleteProduct(String productId);

    ResponseEntity<String> deleteReviewAdmin(String reviewId);
}
