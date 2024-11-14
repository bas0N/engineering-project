package org.example.product.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Image {
    private String thumb;
    private String large;
    private String variant;
    private String hiRes;
}
