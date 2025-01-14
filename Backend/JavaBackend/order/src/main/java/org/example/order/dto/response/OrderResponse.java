package org.example.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.order.dto.CustomerDetails;
import org.example.order.dto.request.DeliverRequest;
import org.example.order.enums.Status;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private String uuid;
    private String orders;
    private Status status;
    private CustomerDetails customerDetails;
    private AddressResponse address;
    private DeliverRequest deliver;
    private List<ItemResponse> items;
    private double summaryPrice;
    private String basketId;
    private String orderId;
    private String ClientSecret;
}
