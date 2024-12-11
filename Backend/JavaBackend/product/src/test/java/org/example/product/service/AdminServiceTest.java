package org.example.product.service;

import org.example.product.entity.Product;
import org.example.product.entity.Review;
import org.example.product.repository.ProductRepository;
import org.example.product.repository.ReviewRepository;
import org.example.product.service.impl.AdminServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {
    @InjectMocks
    private AdminServiceImpl adminService;
    @Mock
    private ProductRepository productRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    private String productId;

    private String reviewId;

    @BeforeEach
    void setUp() {
        productId = "product123";
        reviewId = "review123";
    }

    @Test
    void deleteProduct_Success() {
        // Arrange
        when(productRepository.findByParentAsin(productId)).thenReturn(Optional.of(new Product()));

        // Act
        ResponseEntity<String> response = adminService.deleteProduct(productId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Product with ID: " + productId + " has been successfully deleted."));

        // Verify interactions with mocks
        verify(productRepository, times(1)).findByParentAsin(productId);
        verify(mongoTemplate, times(1)).updateMulti(any(Query.class), any(Update.class), eq(Product.class));
    }

    @Test
    void deleteReview_Success(){
        // Arrange
        Review review = new Review();
        review.setAsin("productAsin123");
        review.setRating(4);

        Product product = new Product();
        product.setParentAsin("productAsin123");
        product.setAverageRating(4.5);
        product.setRatingNumber(10);

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(productRepository.findByParentAsin(review.getAsin())).thenReturn(Optional.of(product));

        // Act
        ResponseEntity<String> response = adminService.deleteReviewAdmin(reviewId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).contains("Review deleted successfully"));

        // Verify interactions with mocks
        verify(reviewRepository, times(1)).findById(reviewId);
        verify(productRepository, times(2)).findByParentAsin(review.getAsin());
        verify(reviewRepository, times(1)).delete(review);
    }

}
