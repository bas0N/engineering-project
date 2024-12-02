package org.example.product.service;

import org.springframework.http.ResponseEntity;

public interface AdminService {
    ResponseEntity<?> deleteProduct(String productId);
}
