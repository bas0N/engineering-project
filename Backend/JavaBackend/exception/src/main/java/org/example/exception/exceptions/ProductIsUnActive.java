package org.example.exception.exceptions;

public class ProductIsUnActive extends RuntimeException {
    public ProductIsUnActive(String productId) {
        super(String.format("Product with ID: %s is not active", productId));
    }

}
