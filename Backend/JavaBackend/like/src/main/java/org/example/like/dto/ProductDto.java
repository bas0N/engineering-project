package org.example.like.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private String uuid;

    private String title;

    private String price;

    private Integer ratingNumber;

    private Double averageRating;

    private List<ImageDto> images;
}
