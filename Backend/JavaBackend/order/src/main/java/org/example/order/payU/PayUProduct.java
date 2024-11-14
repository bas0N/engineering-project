package org.example.order.payU;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class PayUProduct {
    private String name;
    private long unitPrice;
    private long quantity;
}