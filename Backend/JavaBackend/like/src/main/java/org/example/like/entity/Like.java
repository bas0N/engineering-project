package org.example.like.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.like.dto.LikeDto;

@Builder
@Table(name = "likes")
@Entity
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String likeId;

    @Column(
            name = "user_id",
            nullable = false
    )
    private String userId;

    @Column(
            name = "product_id",
            nullable = false
    )
    private String productId;

    @Column(
            name = "date_added",
            nullable = false
    )
    private String dateAdded;
}
