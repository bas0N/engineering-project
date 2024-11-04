package org.example.like.entity;

import jakarta.persistence.*;
import lombok.*;

import java.awt.*;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Table(name = "products")
@Entity
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(generator = "products_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "products_id_seq",sequenceName = "products_id_seq",allocationSize = 1)
    private long id;

    private String uuid;

    private String title;

    private String price;

    private Integer ratingNumber;

    private Double averageRating;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Image> images;

    public Product(String productId, String title, String price, Integer ratingNumber, Double averageRating, List<Image> images) {
        this.title = title;
        this.price = price;
        this.ratingNumber = ratingNumber;
        this.averageRating = averageRating;
        this.images = images;
        this.uuid = productId;
    }


}
