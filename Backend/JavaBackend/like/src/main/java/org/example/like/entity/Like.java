package org.example.like.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.UUID;


@Table(name = "likes")
@Entity
@Setter
@Getter
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "likes_id_seq")
    @SequenceGenerator(name = "likes_id_seq", sequenceName = "likes_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "uuid", updatable = false, nullable = false, unique = true)
    private String uuid;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "date_added", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateAdded;

    public Like() {
        generateUuid();
    }

    public Like(String userId, Product product, Date dateAdded) {
        this.uuid = UUID.randomUUID().toString();
        this.userId = userId;
        this.product = product;
        this.dateAdded = dateAdded;
    }

    private void generateUuid(){
        if (uuid == null || uuid.isEmpty()){
            setUuid(UUID.randomUUID().toString());
        }
    }

}
