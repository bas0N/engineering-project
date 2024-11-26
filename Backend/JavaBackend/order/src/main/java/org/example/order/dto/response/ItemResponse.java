package org.example.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponse {
    private String uuid;
    private String name;
    private String imageUrl;
    private long quantity;
    private double priceUnit;
    private double priceSummary;

}
