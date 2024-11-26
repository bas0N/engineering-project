package org.example.order.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    //private String uuid;
    //private String orders;
    //private Status status;
    private AddressRequest addressRequest;
    private String deliverId;
    private String basketId;
}
