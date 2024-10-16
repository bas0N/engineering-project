package org.example.like.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Table(name = "images")
@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "images_id_seq")
    @SequenceGenerator(name = "images_id_seq", sequenceName = "images_id_seq", allocationSize = 1)
    private Long id;

    private String thumb;
    private String large;
    private String variant;
    private String hiRes;


    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
