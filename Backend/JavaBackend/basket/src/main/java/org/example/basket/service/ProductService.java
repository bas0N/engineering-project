package org.example.basket.service;

import org.example.commondto.BasketProductEvent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface ProductService {
    BasketProductEvent getProductById(String productId) throws ExecutionException, InterruptedException, TimeoutException;
}
