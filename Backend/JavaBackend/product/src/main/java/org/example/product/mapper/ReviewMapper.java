package org.example.product.mapper;

import org.example.product.dto.Request.CreateReviewRequest;
import org.example.product.dto.Response.ReviewResponse;
import org.example.product.entity.Review;
import org.example.product.entity.User;
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

    @Mapping(target = "verifiedPurchase", constant = "false")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "timestamp", source = "timestamp")
    @Mapping(target = "parentAsin", source = "productId")
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "helpfulVote", constant = "0")
    @Mapping(target = "asin", source = "productId")
    Review toReview(CreateReviewRequest createReviewRequest, String productId, double timestamp, User user);
}
