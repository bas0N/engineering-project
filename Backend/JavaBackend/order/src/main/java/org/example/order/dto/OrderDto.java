package org.example.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.order.enums.Status;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderDto {
    private String uuid;
    private String orders;
    private Status status;
    private CustomerDetails customerDetails;
    private Address address;
    private DeliverDto deliver;
    private List<Items> items;
    private double summaryPrice;
}
