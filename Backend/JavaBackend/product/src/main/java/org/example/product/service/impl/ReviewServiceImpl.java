package org.example.product.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.commondto.UserDetailInfoEvent;
import org.example.exception.exceptions.*;
import org.example.jwtcommon.jwt.JwtCommonService;
import org.example.product.dto.Request.CreateReviewRequest;
import org.example.product.dto.Response.ReviewResponse;
import org.example.product.entity.Review;
import org.example.product.mapper.ReviewMapper;
import org.example.product.mapper.UserMapper;
import org.example.product.repository.ProductRepository;
import org.example.product.repository.ReviewRepository;
import org.example.product.service.ReviewService;
import org.example.product.service.UserService;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final JwtCommonService jwtCommonService;
    private final UserService userService;
    private final ProductRepository productRepository;

    @Override
    public ResponseEntity<Page<ReviewResponse>> getReviews(String productId, int page, int size, boolean ascending) {
        try {
            if (page < 0 || size <= 0) {
                throw new IllegalArgumentException("Page index must be >= 0 and size must be > 0");
            }

            Pageable pageable = PageRequest.of(page, size);
            Page<Review> reviewPage = reviewRepository.findAllByParentAsin(productId, pageable, ascending);

            if (reviewPage.isEmpty()) {
                throw new ResourceNotFoundException("Reviews", "productId", productId);
            }

            Page<ReviewResponse> responsePage = reviewPage.map(
                    review -> ReviewMapper.INSTANCE.toReviewResponse(review, new Date((long) review.getTimestamp()))
            );
            return ResponseEntity.ok(responsePage);

        } catch (ResourceNotFoundException e) {
            log.error("No reviews found for product with ID: {}", productId, e);
            throw new ResourceNotFoundException("Reviews", "productId", productId);
        } catch (IllegalArgumentException e) {
            log.error("Invalid pagination parameters for reviews of product with ID: {}", productId, e);
            throw new IllegalArgumentException("Invalid pagination parameters for reviews");
        } catch (DataAccessException e) {
            log.error("Database access error while retrieving reviews for product with ID: {}", productId, e);
            throw new DatabaseAccessException("Error accessing the review database", e);
        } catch (Exception e) {
            log.error("Unexpected error while retrieving reviews for product with ID: {}", productId, e);
            throw new UnExpectedError("An unexpected error occurred while retrieving reviews", e);
        }
    }

    @Override
    public ResponseEntity<ReviewResponse> getReview(String reviewId) {
        try {
            Review review = reviewRepository.findById(reviewId)
                    .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

            ReviewResponse reviewResponse = ReviewMapper.INSTANCE.toReviewResponse(review, new Date((long) review.getTimestamp()));

            return ResponseEntity.ok(reviewResponse);

        } catch (ResourceNotFoundException e) {
            log.error("Review not found with ID: {}", reviewId, e);
            throw new ResourceNotFoundException("Review", "id", reviewId);
        } catch (DataAccessException e) {
            log.error("Database error while retrieving review with ID: {}", reviewId, e);
            throw new DatabaseAccessException("Error accessing the review database", e);
        } catch (Exception e) {
            log.error("Unexpected error while retrieving review with ID: {}", reviewId, e);
            throw new UnExpectedError("An unexpected error occurred while retrieving the review", e);
        }
    }

    @Override
    public ResponseEntity<ReviewResponse> createReview(String productId, CreateReviewRequest createReviewRequest, HttpServletRequest request) {
        try {
            String userId = jwtCommonService.getUserFromRequest(request);

            UserDetailInfoEvent userInfo = userService.getUserDetailInfo(userId);

            Review review = buildReview(productId, createReviewRequest, userId, userInfo);

            addProductRating(productId, createReviewRequest.getRating());

            review = reviewRepository.save(review);

            return ResponseEntity.ok(ReviewMapper.INSTANCE.toReviewResponse(review, new Date((long) review.getTimestamp())));

        } catch (DataAccessException e) {
            log.error("Database access error while creating review for productId: {}", productId, e);
            throw new DatabaseAccessException("Error accessing the review database", e);
        } catch (ApiRequestException e) {
            log.error("Failed to create review for productId: {}", productId, e);
            throw new ApiRequestException("Failed to create review", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while creating review for productId: {}", productId, e);
            throw new UnExpectedError("An unexpected error occurred while creating the review", e);
        }
    }

    @Override
    public ResponseEntity<ReviewResponse> updateReview(String reviewId, CreateReviewRequest createReviewRequest, HttpServletRequest request) {
        try {
            String userId = jwtCommonService.getUserFromRequest(request);

            Review review = reviewRepository.findById(reviewId)
                    .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
            int oldRating = review.getRating();

            validateUserAuthorization(userId, review);

            updateReviewFields(review, createReviewRequest);

            updateProductRating(review.getAsin(), createReviewRequest.getRating(), oldRating);

            Review updatedReview = reviewRepository.save(review);

            return ResponseEntity.ok(ReviewMapper.INSTANCE.toReviewResponse(updatedReview, new Date((long) updatedReview.getTimestamp() * 1000)));

        } catch (ResourceNotFoundException e) {
            log.error("Review not found with ID: {}", reviewId, e);
            throw new ResourceNotFoundException("Review", "id", reviewId);
        } catch (ApiRequestException e) {
            log.error("Unauthorized attempt to update review with ID: {}", reviewId, e);
            throw e;
        } catch (DataAccessException e) {
            log.error("Database access error while updating review with ID: {}", reviewId, e);
            throw new DatabaseAccessException("Error accessing the review database", e);
        } catch (Exception e) {
            log.error("Unexpected error while updating review with ID: {}", reviewId, e);
            throw new UnExpectedError("An unexpected error occurred while updating the review", e);
        }
    }

    @Override
    public ResponseEntity<?> deleteReview(String reviewId) {
        try {
            Review review = reviewRepository.findById(reviewId)
                    .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

            deleteProductRating(review.getAsin(), review.getRating());
            reviewRepository.delete(review);

            return ResponseEntity.ok("Review deleted successfully");

        } catch (ResourceNotFoundException e) {
            log.error("Review not found with ID: {}", reviewId, e);
            throw new ResourceNotFoundException("Review", "id", reviewId);
        } catch (DataAccessException e) {
            log.error("Database access error while deleting review with ID: {}", reviewId, e);
            throw new DatabaseAccessException("Error accessing the review database", e);
        } catch (Exception e) {
            log.error("Unexpected error while deleting review with ID: {}", reviewId, e);
            throw new ApiRequestException("An unexpected error occurred while deleting the review", e.getMessage());
        }
    }

    private ResponseEntity<ErrorDetails> buildErrorResponse(String message, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(new ErrorDetails(new Date(), message, null));
    }

    private Review buildReview(String productId, CreateReviewRequest createReviewRequest, String userId, UserDetailInfoEvent userInfo) {
        return new Review(
                null,
                productId,
                0,
                null,
                productId,
                createReviewRequest.getRating(),
                createReviewRequest.getText(),
                createReviewRequest.getTitle(),
                userId,
                System.currentTimeMillis(),
                false,
                UserMapper.INSTANCE.toUser(userInfo)
        );
    }

    private void validateUserAuthorization(String userId, Review review) {
        if (!review.getUser().getUserId().equals(userId)) {
            throw new ApiRequestException("You are not authorized to update this review", "UNAUTHORIZED");
        }
    }

    private void updateReviewFields(Review review, CreateReviewRequest createReviewRequest) {
        if (createReviewRequest.getTitle() != null) {
            review.setTitle(createReviewRequest.getTitle());
        }
        if (createReviewRequest.getText() != null) {
            review.setText(createReviewRequest.getText());
        }
        review.setRating(createReviewRequest.getRating());
    }

    private void addProductRating(String productId, int rating) {
        productRepository.findByParentAsin(productId)
                .ifPresent(product -> {
                    double newRating = (product.getAverageRating() * product.getRatingNumber() + rating) / (product.getRatingNumber() + 1);
                    product.setAverageRating(newRating);
                    product.setRatingNumber(product.getRatingNumber() + 1);
                    productRepository.save(product);
                });
    }

    private void updateProductRating(String asin, int rating, int oldRating) {
        productRepository.findByParentAsin(asin)
                .ifPresent(product -> {
                    double newRating = (product.getAverageRating() * product.getRatingNumber() + rating - oldRating) / product.getRatingNumber();
                    product.setAverageRating(newRating);
                    productRepository.save(product);
                });
    }

    private void deleteProductRating(String asin, int rating) {
        productRepository.findByParentAsin(asin)
                .ifPresent(product -> {
                    double newRating = (product.getAverageRating() * product.getRatingNumber() - rating) / (product.getRatingNumber() - 1);
                    product.setAverageRating(newRating);
                    product.setRatingNumber(product.getRatingNumber() - 1);
                    productRepository.save(product);
                });
    }
}
