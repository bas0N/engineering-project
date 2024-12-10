package org.example.product.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "reviews_Health_and_Personal_Care")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Review {
    @Id
    private String id;

    @Field("asin")
    private String asin;

    @Field("helpful_vote")
    private int helpfulVote;

    @Field("images")
    private List<ImageReview> images;

    @Field("parent_asin")
    private String parentAsin;

    @Field("rating")
    private int rating;

    @Field("text")
    private String text;

    @Field("title")
    private String title;

    @Field("user_id")
    private String userId;

    @Field("timestamp")
    private double timestamp;

    @Field("verified_purchase")
    private boolean verifiedPurchase;

    @Field("user")
    private User user;
}
