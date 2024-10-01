package org.example.like.service.impl;

import org.example.like.dto.LikeDto;
import org.example.like.entity.Like;
import org.example.like.mapper.LikeMapper;
import org.example.like.repository.LikeRepository;
import org.example.like.response.LikeResponse;
import org.example.like.service.LikeService;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl implements LikeService {
    private LikeRepository likeRepository;

    @Override
    public LikeResponse addLike(String productId) {
        String userId = "user_id";
        Like like = LikeMapper.INSTANCE.mapLikeDtoToLike(userId, productId);
        likeRepository.save(like);
        return LikeMapper.INSTANCE.mapLikeToLikeResponse(like);
    }

    @Override
    public LikeDto getMyLike() {
        return null;
    }
}
