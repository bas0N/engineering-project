package org.example.product.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReviewResponse {
    private String id;
    private String title;
    private String text;
    private String userFirstName;
    private String userLastName;
    private String email;
    private double timestamp;
    private int helpful_vote;
    private boolean verified_purchase;
}
