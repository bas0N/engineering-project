package org.example.like.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.like.dto.LikeDto;
import org.example.like.entity.Product;
import org.example.like.response.LikeResponse;

import java.util.List;

public interface LikeService {
    LikeResponse addLike(String productId, HttpServletRequest request);

    List<Product> getMyLikedProducts(HttpServletRequest request);

    Object getNumberOfLikes(String productId);

    void removeLike(String likeId);
}
