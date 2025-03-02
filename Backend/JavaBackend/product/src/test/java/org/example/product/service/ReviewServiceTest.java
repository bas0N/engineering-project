package org.example.product.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.commondto.UserDetailInfoEvent;
import org.example.commonutils.Utils;
import org.example.exception.exceptions.ResourceNotFoundException;
import org.example.product.dto.Request.CreateReviewRequest;
import org.example.product.dto.Response.ReviewResponse;
import org.example.product.entity.Product;
import org.example.product.entity.Review;
import org.example.product.entity.User;
import org.example.product.repository.ProductRepository;
import org.example.product.repository.ReviewRepository;
import org.example.product.service.impl.ReviewServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {
    @InjectMocks
    private ReviewServiceImpl reviewService;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private Utils utils;
    @Mock
    private UserService userService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private ProductRepository productRepository;

    @Test
    void getReviews_Success() {
        // Arrange
        String productId = "product123";
        int page = 0;
        int size = 10;
        boolean ascending = true;

        Review review = new Review();
        review.setId("review1");
        review.setParentAsin(productId);
        review.setTitle("Great product!");
        review.setText("Great product!");
        review.setTimestamp(1632990000);

        Page<Review> mockReviews = new PageImpl<>(List.of(review), PageRequest.of(page, size), 1);
        when(reviewRepository.findAllByParentAsin(productId, PageRequest.of(page, size), ascending))
                .thenReturn(mockReviews);

        // Act
        ResponseEntity<?> response = reviewService.getReviews(productId, page, size, ascending);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        Page<ReviewResponse> responsePage = (Page<ReviewResponse>) response.getBody();
        assertEquals(1, responsePage.getTotalElements());
        assertEquals("Great product!", responsePage.getContent().getFirst().getTitle());
    }


    @Test
    void getReview_Success() {
        // Arrange
        String reviewId = "review123";
        Review review = new Review();
        review.setId(reviewId);
        review.setParentAsin("product123");
        review.setText("Great product!");
        review.setTimestamp(1632990000);

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        // Act
        ResponseEntity<?> response = reviewService.getReview(reviewId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        ReviewResponse reviewResponse = (ReviewResponse) response.getBody();
        assertEquals(reviewId, reviewResponse.getId());
        assertEquals("Great product!", reviewResponse.getText());
    }


    @Test
    void createReview_Success() {
        // Arrange
        String productId = "product123";
        String userId = "user123";
        CreateReviewRequest createReviewRequest = new CreateReviewRequest();
        createReviewRequest.setRating(4);
        createReviewRequest.setText("Great product!");
        createReviewRequest.setTitle("Amazing");

        UserDetailInfoEvent userInfo = new UserDetailInfoEvent("user123", "test@example.com", "John", "Doe", "user123", true);

        Review review = new Review();
        review.setId("review123");
        review.setParentAsin(productId);
        review.setUserId(userId);
        review.setRating(4);
        review.setText("Great product!");
        review.setTimestamp(1632990000);

        Product product = new Product();
        product.setParentAsin(productId);
        product.setAverageRating(4.0);
        product.setRatingNumber(10);

        MockHttpServletRequest request = new MockHttpServletRequest();
        when(utils.extractUserIdFromRequest(any(HttpServletRequest.class))).thenReturn(userId);
        when(userService.getUserDetailInfo(userId)).thenReturn(userInfo);
        when(productRepository.findByParentAsin(productId)).thenReturn(Optional.of(product));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        // Act
        ResponseEntity<?> response = reviewService.createReview(productId, createReviewRequest, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        ReviewResponse reviewResponse = (ReviewResponse) response.getBody();
        assertEquals("review123", reviewResponse.getId());
        assertEquals("Great product!", reviewResponse.getText());
        assertEquals(4, reviewResponse.getRating());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void updateReview_Success() {
        // Arrange
        String reviewId = "review123";
        String userId = "user123";
        String asin = "product123";

        User user = new User();
        user.setUserId(userId);

        Review existingReview = new Review();
        existingReview.setId(reviewId);
        existingReview.setUserId(userId);
        existingReview.setUser(user);
        existingReview.setAsin(asin);
        existingReview.setRating(4);

        Product product = new Product();
        product.setParentAsin(asin);
        product.setAverageRating(4.5);
        product.setRatingNumber(20);

        CreateReviewRequest createReviewRequest = new CreateReviewRequest();
        createReviewRequest.setRating(5);
        createReviewRequest.setText("Updated review content!");

        MockHttpServletRequest request = new MockHttpServletRequest();
        when(utils.extractUserIdFromRequest(any(HttpServletRequest.class))).thenReturn(userId);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(existingReview));
        when(productRepository.findByParentAsin(asin)).thenReturn(Optional.of(product));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ResponseEntity<?> response = reviewService.updateReview(reviewId, createReviewRequest, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ReviewResponse reviewResponse = (ReviewResponse) response.getBody();
        assertNotNull(reviewResponse);
        assertEquals(5, reviewResponse.getRating());
        assertEquals("Updated review content!", reviewResponse.getText());

        verify(reviewRepository, times(1)).save(any(Review.class));
        verify(productRepository, times(1)).save(product);
    }

}
