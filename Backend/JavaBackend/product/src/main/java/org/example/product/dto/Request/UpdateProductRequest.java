package org.example.product.dto.Request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.example.product.enums.DetailKey;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class UpdateProductRequest {
    @Size(max = 255, message = "Title must be less than 255 characters")
    private String title;

    private List<String> categories;

    private List<String> description;

    private Map<DetailKey, String> details;

    private List<String> features;

    private String mainCategory;

    @Positive(message = "Price must be bigger than 0")
    private Double price;

    private String store;
}
