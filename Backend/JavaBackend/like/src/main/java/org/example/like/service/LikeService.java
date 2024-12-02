package org.example.like.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.like.dto.ProductResponse;
import org.example.like.response.LikeResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface LikeService {
    ResponseEntity<LikeResponse> addLike(String productId, HttpServletRequest request);

    List<ProductResponse> getMyLikedProducts(HttpServletRequest request);

    Long getNumberOfLikes(String productId);

    void removeLike(String likeId, HttpServletRequest request);

    Boolean isLiked(String productId, HttpServletRequest request);
}

