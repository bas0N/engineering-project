package org.example.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BasketItemDto {
    private String uuid;
    private String name;
    private long quantity;
    private String imageUrl;
    private double price;
    private double summaryPrice;
}
