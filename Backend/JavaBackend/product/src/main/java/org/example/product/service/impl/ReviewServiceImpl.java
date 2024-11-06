package org.example.product.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.jwtcommon.jwt.JwtCommonService;
import org.example.product.dto.Request.CreateReviewRequest;
import org.example.product.dto.Response.ReviewResponse;
import org.example.product.entity.Review;
import org.example.product.repository.ReviewRepository;
import org.example.product.service.ImageService;
import org.example.product.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final JwtCommonService jwtCommonService;
    private final ImageService imageService;

    @Override
    public Page<ReviewResponse> getReviews(String productId) {
        List<Review> reviewList = reviewRepository.findAllByParent_asin(productId);
        List<ReviewResponse> reviewResponses = reviewList.stream().map(review -> new ReviewResponse(
                review.getId(),
                review.getTitle(),
                review.getText(),
                "fn",
                "ln",
                "email",
                review.getTimestamp(),
                review.getHelpful_vote(),
                review.isVerified_purchase()
        )).toList();
        //return reviewResponses;
        return null;
    }

    @Override
    public ReviewResponse getReview(String reviewId) {
        Review review = reviewRepository.findById(reviewId).orElse(null);
        if (review == null) {
            //return ResponseEntity.notFound().build();
            return null;
        }
        ReviewResponse reviewResponse = new ReviewResponse(
                review.getId(),
                review.getTitle(),
                review.getText(),
                "fn",
                "ln",
                "email",
                review.getTimestamp(),
                review.getHelpful_vote(),
                review.isVerified_purchase()
        );
        //return ReviewResponse;
        return null;
    }

    @Override
    public ReviewResponse createReview(String productId, CreateReviewRequest createReviewRequest, HttpServletRequest request) {
        String token = jwtCommonService.getTokenFromRequest(request);
        String userId = jwtCommonService.getCurrentUserId(token);
        Review review = new Review(
                null,
                productId,
                0,
//                imageService.uploadImages(addProductRequest.getImages()),
                null,
                productId,
                0,
                createReviewRequest.getText(),
                createReviewRequest.getTitle(),
                userId,
                0,
                false
        );
        reviewRepository.save(review);
        //return ResponseEntity.ok().build();
        return null;
    }

    @Override
    public ReviewResponse updateReview(String reviewId, CreateReviewRequest createReviewRequest, HttpServletRequest request) {
        String token = jwtCommonService.getTokenFromRequest(request);
        String userId = jwtCommonService.getCurrentUserId(token);
        return null;
    }

    @Override
    public Boolean deleteReview(String reviewId) {
        return true;
    }
}
