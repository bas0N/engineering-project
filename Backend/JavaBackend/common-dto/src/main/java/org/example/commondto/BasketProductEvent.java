package org.example.commondto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class BasketProductEvent {
    private String id;
    private String name;
    private double price;
    private List<String> imageUrls;
}
