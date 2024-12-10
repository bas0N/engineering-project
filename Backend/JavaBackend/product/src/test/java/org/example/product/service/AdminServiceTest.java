package org.example.product.service;

import org.example.product.entity.Product;
import org.example.product.repository.ProductRepository;
import org.example.product.service.impl.AdminServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {
    @InjectMocks
    private AdminServiceImpl adminService;
    @Mock
    private ProductRepository productRepository;

    @Test
    void deleteProduct_Success() {
        // Arrange
        String productId = "product123";

        // Mocking the repository to simulate product existence
        when(productRepository.findByParentAsin(productId)).thenReturn(Optional.of(new Product()));

        // Act
        ResponseEntity<?> response = adminService.deleteProduct(productId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().toString().contains("Product with ID: " + productId + " has been successfully deleted."));

        // Verifying the repository calls
        verify(productRepository, times(1)).findByParentAsin(productId);
        verify(productRepository, times(1)).deleteByParentAsin(productId);
    }

}
