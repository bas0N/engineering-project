package org.example.product.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Map;

@Document(collection = "meta_Health_and_Personal_Care")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    private String id;

    @Field("user_id")
    private String userId;

    @Field("bought_together")
    private String boughtTogether;

    @Field("categories")
    private List<String> categories;

    @Field("description")
    private List<String> description;

    @Field("details")
    private Map<String, String> details;

    @Field("features")
    private List<String> features;

    @Field("images")
    private List<Image> images;

    @Field("main_category")
    private String mainCategory;

    @Field("parent_asin")
    private String parentAsin;

    @Field("price")
    private String price;

    @Field("rating_number")
    private Integer ratingNumber;

    @Field("store")
    private String store;

    @Field("title")
    private String title;

    @Field("videos")
    private List<Video> videos;

    @Field("average_rating")
    private Double averageRating;

    @Field("user")
    private User user;
}
