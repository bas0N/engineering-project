package org.example.product.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document(collection = "meta_Health_and_Personal_Care")
@Getter
@Setter
@AllArgsConstructor
public class Product {
    @Id
    private String id; //to

    private String userId;

    private String boughtTogether; //to papa

    private List<String> categories; //to

    private List<String> description;

    private Map<String, String> details;

    private List<String> features;

    private List<Image> images; // to

    private String mainCategory; //to

    private String parentAsin;

    private String price; //to

    private Integer ratingNumber; //to

    private String store; //to

    private String title; //to

    private List<Video> videos;

    private Double averageRating; //to
}
