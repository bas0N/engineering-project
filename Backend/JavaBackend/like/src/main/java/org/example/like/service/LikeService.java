package org.example.like.service;

import org.example.like.dto.LikeDto;
import org.example.like.response.LikeResponse;

public interface LikeService {
    LikeResponse addLike(String productId);

    LikeDto getMyLike();
}
