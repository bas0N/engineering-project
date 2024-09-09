package org.example.product.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document(collection = "products")
@Getter
@Setter
public class Product {
    @Id
    private String id;

    private String boughtTogether;

    private List<String> categories;

    private List<String> description;

    private Map<String, String> details;

    private List<String> features;

    private List<Image> images;

    private String mainCategory;

    private String parentAsin;

    private String price;

    private Integer ratingNumber;

    private String store;

    private String title;

    private List<Video> videos;

    private Double averageRating;
}
