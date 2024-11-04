package org.example.product.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.product.entity.Review;
import org.example.product.repository.ReviewRepository;
import org.example.product.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewService reviewService;
    private final ReviewRepository reviewRepository;

    @Override
    public ResponseEntity<?> getReviews(String productId) {
        List<Review> reviewList = reviewRepository.findAllByParent_asin(productId);

    }

    @Override
    public ResponseEntity<?> getReview(String productId) {
        return null;
    }
}
