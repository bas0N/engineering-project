package org.example.product.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exception.exceptions.ErrorDetails;
import org.example.exception.exceptions.ResourceNotFoundException;
import org.example.exception.exceptions.UnauthorizedException;
import org.example.product.entity.Product;
import org.example.product.repository.ProductRepository;
import org.example.product.service.AdminService;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {
    private final ProductRepository productRepository;

    @Override
    public ResponseEntity<?> deleteProduct(String id) {
        try {
            productRepository.findByParentAsin(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "ID", id));
            productRepository.deleteByParentAsin(id);

            return ResponseEntity.ok("Product with ID: " + id + " has been successfully deleted.");
        } catch (ResourceNotFoundException e) {
            log.error("Product not found with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorDetails(new Date(), e.getMessage(), null));
        } catch (DataAccessException e) {
            log.error("Database access error while deleting product with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDetails(new Date(), "Error accessing the product database", null));
        } catch (Exception e) {
            log.error("Unexpected error while deleting product with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDetails(new Date(), "An unexpected error occurred while deleting product", null));
        }
    }
}
