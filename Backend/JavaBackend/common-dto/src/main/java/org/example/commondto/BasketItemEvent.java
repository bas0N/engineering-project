package org.example.commondto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasketItemEvent {
    private String uuid;
    private String name;
    private long quantity;
    private String imageUrl;
    private double price;
    private double summaryPrice;
}
