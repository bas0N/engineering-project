package org.example.commondto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductHistoryEvent {
    private String productId;
    private String userId;
    private Date timeStamp;
}
