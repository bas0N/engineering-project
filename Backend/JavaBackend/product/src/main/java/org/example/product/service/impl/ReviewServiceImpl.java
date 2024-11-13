package org.example.product.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.commondto.UserDetailInfoEvent;
import org.example.exception.exceptions.ApiRequestException;
import org.example.exception.exceptions.DatabaseAccessException;
import org.example.exception.exceptions.ResourceNotFoundException;
import org.example.jwtcommon.jwt.JwtCommonService;
import org.example.product.dto.Request.CreateReviewRequest;
import org.example.product.dto.Response.ReviewResponse;
import org.example.product.entity.Review;
import org.example.product.kafka.user.UserEventConsumer;
import org.example.product.kafka.user.UserEventProducer;
import org.example.product.mapper.ReviewMapper;
import org.example.product.mapper.UserMapper;
import org.example.product.repository.ReviewRepository;
import org.example.product.service.ImageService;
import org.example.product.service.ReviewService;
import org.jetbrains.annotations.NotNull;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final JwtCommonService jwtCommonService;
    private final UserEventConsumer userEventConsumer;
    private final UserEventProducer userEventProducer;
    private final ImageService imageService;

    @Override
    public Page<ReviewResponse> getReviews(String productId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviewPage = reviewRepository.findAllByParent_asin(productId, pageable);
        return reviewPage.map(ReviewMapper.INSTANCE::toReviewResponse);
    }

    @Override
    public ReviewResponse getReview(String reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

        return ReviewMapper.INSTANCE.toReviewResponse(review);
    }

    @Override
    public ReviewResponse createReview(String productId, CreateReviewRequest createReviewRequest, HttpServletRequest request) {
        String userId = null;
        try {
            Review review = getReview(productId, createReviewRequest, request);
            review = reviewRepository.save(review);
            return ReviewMapper.INSTANCE.toReviewResponse(review);
        } catch (DataAccessException e) {
            log.error("Database access error while creating review", e);
            throw new DatabaseAccessException("Error accessing the review database", e);
        } catch (Exception e) {
            log.error("Unexpected error while creating review", e);
            throw new ApiRequestException("An unexpected error occurred while creating review");
        }
    }

    @NotNull
    private Review getReview(String productId, CreateReviewRequest createReviewRequest, HttpServletRequest request) {
        String userId;
        userId = jwtCommonService.getUserFromRequest(request);
        userEventProducer.sendUserEvent(userId);

        CompletableFuture<UserDetailInfoEvent> userFuture = userEventConsumer.getUserDetails(userId);
        UserDetailInfoEvent userInfo = userFuture.join();

        Review review = new Review(
                null,
                productId,
                0,
                imageService.uploadReviewImages(createReviewRequest.getImages()),
                productId,
                createReviewRequest.getRating(),
                createReviewRequest.getText(),
                createReviewRequest.getTitle(),
                userId,
                System.currentTimeMillis(),
                false,
                UserMapper.INSTANCE.toUser(userInfo)
        );
        return review;
    }

    @Override
    public ReviewResponse updateReview(String reviewId, CreateReviewRequest createReviewRequest, HttpServletRequest request) {
        try {
            String userId = jwtCommonService.getUserFromRequest(request);
            Review review = reviewRepository.findById(reviewId)
                    .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

            if (!review.getUser().getUserId().equals(userId)) {
                throw new ApiRequestException("You are not authorized to update this review");
            }
            if (createReviewRequest.getTitle() != null) {
                review.setTitle(createReviewRequest.getTitle());
            }
            if (createReviewRequest.getText() != null) {
                review.setText(createReviewRequest.getText());
            }
            review.setRating(createReviewRequest.getRating());
            if (createReviewRequest.getImages() != null) {
                review.setImages(imageService.uploadReviewImages(createReviewRequest.getImages()));
            }

            Review updatedReview = reviewRepository.save(review);
            return ReviewMapper.INSTANCE.toReviewResponse(updatedReview);
        } catch (DataAccessException e) {
            log.error("Database access error while updating review", e);
            throw new DatabaseAccessException("Error accessing the review database", e);
        } catch (Exception e) {
            log.error("Unexpected error while updating review", e);
            throw new ApiRequestException("An unexpected error occurred while updating the review");
        }
    }

    @Override
    public Boolean deleteReview(String reviewId) {
        try {
            Review review = reviewRepository.findById(reviewId)
                    .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
            reviewRepository.delete(review);
            return true;
        } catch (DataAccessException e) {
            log.error("Database access error while deleting review", e);
            throw new DatabaseAccessException("Error accessing the review database", e);
        } catch (Exception e) {
            log.error("Unexpected error while deleting review", e);
            throw new ApiRequestException("An unexpected error occurred while deleting the review");
        }
    }
}
