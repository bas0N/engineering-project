package org.example.product.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.product.entity.Image;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private String parentAsin;
    private List<String> categories;
    private List<Image> images; // to
    private String mainCategory;
    private Integer ratingNumber; //to
    private String store; //to
    private String title; //to
    private String price;
    private Double averageRating; //to
    private UserResponse owner;
    private List<String> features;
    private List<String> description;
}
