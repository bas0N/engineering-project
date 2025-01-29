package org.example.product.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.product.entity.Image;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailResponse {
    private String parentAsin;
    private List<String> categories;
    private List<Image> images;
    private String mainCategory;
    private Integer ratingNumber;
    private String store;
    private String title;
    private String price;
    private Double averageRating;
    private UserResponse owner;
    private List<String> features;
    private List<String> description;
    private Map<String, String> details;
    private Boolean isActive;
}
