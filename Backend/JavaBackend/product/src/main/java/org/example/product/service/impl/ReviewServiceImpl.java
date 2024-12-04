package org.example.product.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.commondto.UserDetailInfoEvent;
import org.example.exception.exceptions.*;
import org.example.jwtcommon.jwt.Utils;
import org.example.product.dto.Request.CreateReviewRequest;
import org.example.product.dto.Response.ReviewResponse;
import org.example.product.entity.Product;
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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final Utils utils;
    private final UserService userService;
    private final ProductRepository productRepository;
    private final ReviewMapper reviewMapper = ReviewMapper.INSTANCE;
    private final UserMapper userMapper = UserMapper.INSTANCE;

    @Override
    public ResponseEntity<Page<ReviewResponse>> getReviews(String productId, int page, int size, boolean ascending) {
        if (page < 0 || size <= 0) {
            throw new InvalidParameterException(
                    "Page index must be >= 0 and size must be > 0",
                    "INVALID_PAGINATION_PARAMETERS",
                    Map.of("page", page, "size", size)
            );
        }

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Review> reviewPage = reviewRepository.findAllByParentAsin(productId, pageable, ascending);

            if (reviewPage.isEmpty()) {
                throw new ResourceNotFoundException(
                        "Reviews",
                        "productId",
                        productId,
                        "REVIEWS_NOT_FOUND",
                        Map.of("productId", productId)
                );
            }

            Page<ReviewResponse> responsePage = reviewPage.map(
                    review -> reviewMapper.toReviewResponse(review, new Date((long) review.getTimestamp()))
            );
            return ResponseEntity.ok(responsePage);

        } catch (DataAccessException e) {
            log.error("Database access error while retrieving reviews for product with ID: {}", productId, e);
            throw new DatabaseAccessException(
                    "Error accessing the review database",
                    e,
                    "DATABASE_ACCESS_ERROR",
                    Map.of("operation", "getReviews", "productId", productId)
            );
        } catch (Exception e) {
            log.error("Unexpected error while retrieving reviews for product with ID: {}", productId, e);
            throw new UnExpectedError(
                    "An unexpected error occurred while retrieving reviews",
                    e,
                    "GET_REVIEWS_ERROR",
                    Map.of("productId", productId)
            );
        }
    }

    @Override
    public ResponseEntity<ReviewResponse> getReview(String reviewId) {
        try {
            Review review = reviewRepository.findById(reviewId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Review",
                            "id",
                            reviewId,
                            "REVIEW_NOT_FOUND",
                            Map.of("reviewId", reviewId)
                    ));

            ReviewResponse reviewResponse = reviewMapper.toReviewResponse(review, new Date((long) review.getTimestamp()));

            return ResponseEntity.ok(reviewResponse);

        } catch (DataAccessException e) {
            log.error("Database error while retrieving review with ID: {}", reviewId, e);
            throw new DatabaseAccessException(
                    "Error accessing the review database",
                    e,
                    "DATABASE_ACCESS_ERROR",
                    Map.of("operation", "getReview", "reviewId", reviewId)
            );
        } catch (Exception e) {
            log.error("Unexpected error while retrieving review with ID: {}", reviewId, e);
            throw new UnExpectedError(
                    "An unexpected error occurred while retrieving the review",
                    e,
                    "GET_REVIEW_ERROR",
                    Map.of("reviewId", reviewId)
            );
        }
    }

    @Override
    public ResponseEntity<ReviewResponse> createReview(String productId, CreateReviewRequest createReviewRequest, HttpServletRequest request) {
        try {
            String userId = utils.extractUserIdFromRequest(request);

            UserDetailInfoEvent userInfo = userService.getUserDetailInfo(userId);

            if (userInfo == null) {
                throw new ResourceNotFoundException(
                        "User",
                        "ID",
                        userId,
                        "USER_NOT_FOUND",
                        Map.of("userId", userId)
                );
            }

            Product product = productRepository.findByParentAsin(productId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product",
                            "parentAsin",
                            productId,
                            "PRODUCT_NOT_FOUND",
                            Map.of("productId", productId)
                    ));

            Review review = buildReview(productId, createReviewRequest, userId, userInfo);

            addProductRating(product.getParentAsin(), createReviewRequest.getRating());

            review = reviewRepository.save(review);

            return ResponseEntity.ok(reviewMapper.toReviewResponse(review, new Date((long) review.getTimestamp())));

        } catch (ResourceNotFoundException e) {
            log.error("Resource not found: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            log.error("Database access error while creating review for productId: {}", productId, e);
            throw new DatabaseAccessException(
                    "Error accessing the review database",
                    e,
                    "DATABASE_ACCESS_ERROR",
                    Map.of("operation", "createReview", "productId", productId)
            );
        } catch (Exception e) {
            log.error("Unexpected error while creating review for productId: {}", productId, e);
            throw new UnExpectedError(
                    "An unexpected error occurred while creating the review",
                    e,
                    "CREATE_REVIEW_ERROR",
                    Map.of("productId", productId)
            );
        }
    }

    @Override
    public ResponseEntity<ReviewResponse> updateReview(String reviewId, CreateReviewRequest createReviewRequest, HttpServletRequest request) {
        try {
            String userId = utils.extractUserIdFromRequest(request);

            Review review = reviewRepository.findById(reviewId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Review",
                            "id",
                            reviewId,
                            "REVIEW_NOT_FOUND",
                            Map.of("reviewId", reviewId)
                    ));
            int oldRating = review.getRating();

            validateUserAuthorization(userId, review);

            updateReviewFields(review, createReviewRequest);

            Product product = productRepository.findByParentAsin(review.getAsin())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product",
                            "parentAsin",
                            review.getAsin(),
                            "PRODUCT_NOT_FOUND",
                            Map.of("productId", review.getAsin())
                    ));

            updateProductRating(product.getParentAsin(), createReviewRequest.getRating(), oldRating);

            Review updatedReview = reviewRepository.save(review);

            return ResponseEntity.ok(reviewMapper.toReviewResponse(updatedReview, new Date((long) updatedReview.getTimestamp())));

        } catch (ResourceNotFoundException | UnauthorizedException e) {
            log.error("Error updating review: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            log.error("Database access error while updating review with ID: {}", reviewId, e);
            throw new DatabaseAccessException(
                    "Error accessing the review database",
                    e,
                    "DATABASE_ACCESS_ERROR",
                    Map.of("operation", "updateReview", "reviewId", reviewId)
            );
        } catch (Exception e) {
            log.error("Unexpected error while updating review with ID: {}", reviewId, e);
            throw new UnExpectedError(
                    "An unexpected error occurred while updating the review",
                    e,
                    "UPDATE_REVIEW_ERROR",
                    Map.of("reviewId", reviewId)
            );
        }
    }

    @Override
    public ResponseEntity<String> deleteReview(String reviewId, HttpServletRequest request) {
        try {
            String userId = utils.extractUserIdFromRequest(request);

            Review review = reviewRepository.findById(reviewId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Review",
                            "id",
                            reviewId,
                            "REVIEW_NOT_FOUND",
                            Map.of("reviewId", reviewId)
                    ));

            validateUserAuthorization(userId, review);

            Product product = productRepository.findByParentAsin(review.getAsin())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product",
                            "parentAsin",
                            review.getAsin(),
                            "PRODUCT_NOT_FOUND",
                            Map.of("productId", review.getAsin())
                    ));

            deleteProductRating(product.getParentAsin(), review.getRating());

            reviewRepository.delete(review);

            return ResponseEntity.ok("Review deleted successfully");

        } catch (ResourceNotFoundException | UnauthorizedException e) {
            log.error("Error deleting review: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            log.error("Database access error while deleting review with ID: {}", reviewId, e);
            throw new DatabaseAccessException(
                    "Error accessing the review database",
                    e,
                    "DATABASE_ACCESS_ERROR",
                    Map.of("operation", "deleteReview", "reviewId", reviewId)
            );
        } catch (Exception e) {
            log.error("Unexpected error while deleting review with ID: {}", reviewId, e);
            throw new UnExpectedError(
                    "An unexpected error occurred while deleting the review",
                    e,
                    "DELETE_REVIEW_ERROR",
                    Map.of("reviewId", reviewId)
            );
        }
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
                userMapper.toUser(userInfo)
        );
    }

    private void validateUserAuthorization(String userId, Review review) {
        if (!review.getUser().getUserId().equals(userId)) {
            throw new UnauthorizedException(
                    "You are not authorized to perform this action on the review",
                    "UNAUTHORIZED",
                    Map.of("userId", userId, "reviewOwnerId", review.getUser().getUserId())
            );
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
