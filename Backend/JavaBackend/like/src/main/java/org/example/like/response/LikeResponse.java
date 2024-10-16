package org.example.like.response;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Builder
@Getter
public class LikeResponse {
    private String likeId;
    private String userId;
    private String productId;
    private Date dateAdded;
}
