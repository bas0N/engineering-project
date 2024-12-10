package org.example.basket.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BasketItemResponse {
    private String uuid;
    private String productId;
    private long quantity;
    private double price;
    private double totalPrice;
    private String imageUrl;
    private String basketId;
}
