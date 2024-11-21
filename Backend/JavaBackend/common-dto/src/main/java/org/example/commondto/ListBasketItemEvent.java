package org.example.commondto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListBasketItemEvent {
    private List<BasketItemEvent> basketProducts;
    private String basketId;
    private double summaryPrice;
}
