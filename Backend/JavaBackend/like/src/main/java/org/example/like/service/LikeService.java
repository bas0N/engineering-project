package org.example.like.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.like.dto.ProductResponse;
import org.example.like.response.LikeResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface LikeService {
    ResponseEntity<LikeResponse> addLike(String productId, HttpServletRequest request);

    ResponseEntity<List<ProductResponse>> getMyLikedProducts(HttpServletRequest request);

    ResponseEntity<Long> getNumberOfLikes(String productId);

    ResponseEntity<String> removeLike(String likeId, HttpServletRequest request);

    ResponseEntity<Boolean> isLiked(String productId, HttpServletRequest request);
}

