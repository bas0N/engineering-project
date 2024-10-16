package org.example.commondto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductEvent {
    private String productId;

    private String title;

    private String price;

    private Integer ratingNumber;

    private Double averageRating;

    List<ImageEvent> images;
}
