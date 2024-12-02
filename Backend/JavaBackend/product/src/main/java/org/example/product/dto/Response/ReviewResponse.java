package org.example.product.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {
    private String id;
    private String title;
    private String text;
    int rating;
    private String userFirstName;
    private String userLastName;
    private String userId;
    private Date timestamp;
    private int helpfulVote;
    private boolean verifiedPurchase;
}
