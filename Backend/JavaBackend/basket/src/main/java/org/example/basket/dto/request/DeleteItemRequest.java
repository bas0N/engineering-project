package org.example.basket.dto.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeleteItemRequest {
    String basketItemUuid;

    @Min(value = 1, message = "Quantity must be greater than 0")
    Long quantity;
}
