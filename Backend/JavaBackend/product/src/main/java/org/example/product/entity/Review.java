package org.example.product.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "reviews_Health_and_Personal_Care")
@Getter
@Setter
@AllArgsConstructor
public class Review {
    @Id
    private String id;
    private String asin;
    private int helpful_vote;
    private List<ImageReview> images;
    private String parent_asin;
    private int rating;
    private String text;
    private String title;
    private String user_id;
    private double timestamp;
    private boolean verified_purchase;
    private User user;
}
