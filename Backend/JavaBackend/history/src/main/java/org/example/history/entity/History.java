package org.example.history.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(name = "likes")
@Entity
@Setter
@Getter
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "history_id_seq")
    @SequenceGenerator(name = "history_id_seq", sequenceName = "history_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "user_id", nullable = false)
    private String userId;
}
