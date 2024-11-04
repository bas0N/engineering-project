package org.example.basket.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Product {
    private String uuid;
    private String name;
    private double price;
    private String[] imageUrls;

}
