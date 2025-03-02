package org.example.basket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ListBasketItemDto {
    private List<BasketItemDto> basketProducts;
    private double summaryPrice;
    private Long summaryQuantity;
    private String basketId;
}
