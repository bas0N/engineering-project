package org.example.like.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Table(name = "images")
@Entity
@NoArgsConstructor
@AllArgsConstructor
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

    public Image(String thumb, String large, String variant, String hiRes, Product product) {
        this.thumb = thumb;
        this.large = large;
        this.variant = variant;
        this.hiRes = hiRes;
        this.product = product;
    }
}
