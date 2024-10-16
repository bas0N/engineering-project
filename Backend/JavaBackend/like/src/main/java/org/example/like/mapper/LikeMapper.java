package org.example.like.mapper;

import org.example.like.entity.Like;
import org.example.like.response.LikeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LikeMapper {
    LikeMapper INSTANCE = Mappers.getMapper(LikeMapper.class);

    @Mapping(source = "like.uuid", target = "likeId")
    @Mapping(source = "like.userId", target = "userId")
    @Mapping(source = "like.product.uuid", target = "productId")
    @Mapping(source = "like.dateAdded", target = "dateAdded")
    LikeResponse mapLikeToLikeResponse(Like like);
}
