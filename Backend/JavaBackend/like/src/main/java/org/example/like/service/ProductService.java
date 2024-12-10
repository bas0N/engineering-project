package org.example.like.service;

import org.example.commondto.ProductEvent;

public interface ProductService {
    ProductEvent getProduct(String productId, String userUuid);
}
