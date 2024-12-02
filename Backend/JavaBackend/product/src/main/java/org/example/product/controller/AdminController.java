package org.example.product.controller;

import lombok.RequiredArgsConstructor;
import org.example.product.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product/admin")
public class AdminController {
    private final AdminService adminService;

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(String productId) {
        return adminService.deleteProduct(productId);
    }
}
