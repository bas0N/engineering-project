package org.example.product.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class User {
    private String email;
    private String firstName;
    private String lastName;
    private String userId;
}
