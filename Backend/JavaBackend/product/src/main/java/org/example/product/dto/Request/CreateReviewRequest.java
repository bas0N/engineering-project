package org.example.product.dto.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.product.entity.ImageReview;
import org.springframework.data.annotation.Id;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class CreateReviewRequest {
    private List<ImageReviewRequest> images;
    private String text;
    private String title;
    private int rating;
}
