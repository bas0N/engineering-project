package org.example.product.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exception.exceptions.*;
import org.example.product.entity.Product;
import org.example.product.entity.Review;
import org.example.product.repository.ProductRepository;
import org.example.product.repository.ReviewRepository;
import org.example.product.service.AdminService;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    @Transactional
    public ResponseEntity<String> deleteProduct(String id) {
        try {
            productRepository.findByParentAsin(id)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product",
                            "ID",
                            id,
                            "PRODUCT_NOT_FOUND",
                            Map.of("productId", id)
                    ));
            Query reviewQuery = new Query(Criteria.where("parent_asin").is(id));
            mongoTemplate.remove(reviewQuery, Review.class);


            Query query = new Query(Criteria.where("parent_asin").is(id));
            Update update = new Update().set("isActive", false);
            mongoTemplate.updateMulti(query, update, Product.class);

            return ResponseEntity.ok("Product with ID: " + id + " has been successfully deleted.");
        } catch (ResourceNotFoundException e) {
            log.error("Product not found with ID: {}", id, e);
            throw e;
        } catch (DataAccessException e) {
            log.error("Database access error while deleting product with ID: {}", id, e);
            throw new DatabaseAccessException(
                    "Error accessing the product database while deleting product",
                    e,
                    "DATABASE_ACCESS_ERROR",
                    Map.of("operation", "deleteProduct", "productId", id)
            );
        } catch (Exception e) {
            log.error("Unexpected error while deleting product with ID: {}", id, e);
            throw new UnExpectedError(
                    "An unexpected error occurred while deleting product",
                    e,
                    "DELETE_PRODUCT_ERROR",
                    Map.of("productId", id)
            );
        }
    }

    @Override
    @Transactional
    public ResponseEntity<String> deleteReviewAdmin(String reviewId) {
        try {
            Review review = reviewRepository.findById(reviewId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Review",
                            "id",
                            reviewId,
                            "REVIEW_NOT_FOUND",
                            Map.of("reviewId", reviewId)
                    ));


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
