package org.example.like.mapper;

import org.example.like.entity.Like;
import org.example.like.response.LikeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LikeMapper {
    LikeMapper INSTANCE = Mappers.getMapper(LikeMapper.class);

    @Mapping(target = "likeId", ignore = true)
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "productId", target = "productId")
    @Mapping(expression = "java(new java.util.Date())", target = "dateAdded")
    Like mapLikeDtoToLike(String userId, String productId);

    @Mapping(source = "likeId", target = "likeId")
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "productId", target = "productId")
    @Mapping(source = "dateAdded", target = "dateAdded")
    LikeResponse mapLikeToLikeResponse(Like like);
}
