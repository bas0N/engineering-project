package org.example.product.mapper;

import org.example.product.dto.Response.ReviewResponse;
import org.example.product.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Date;

@Mapper
public interface ReviewMapper {
    ReviewMapper INSTANCE = Mappers.getMapper(ReviewMapper.class);


    @Mapping(source = "review.user.firstName", target = "userFirstName")
    @Mapping(source = "review.user.lastName", target = "userLastName")
    @Mapping(source = "review.user.userId", target = "userId")
    @Mapping(source = "timestampDate", target = "timestamp")
    ReviewResponse toReviewResponse(Review review, Date timestampDate);
}
