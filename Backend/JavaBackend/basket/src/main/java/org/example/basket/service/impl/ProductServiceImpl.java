package org.example.basket.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.basket.kafka.product.BasketConsumer;
import org.example.basket.kafka.product.BasketProducer;
import org.example.basket.service.ProductService;
import org.example.commondto.BasketProductEvent;
import org.example.exception.exceptions.DatabaseAccessException;
import org.example.exception.exceptions.UnExpectedError;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final BasketConsumer basketConsumer;
    private final BasketProducer basketProducer;
    @Override
    public BasketProductEvent getProductById(String productId) {
        try {
            CompletableFuture<BasketProductEvent> productFuture = basketConsumer.getProductDetails(productId);
            basketProducer.requestProductDetails(productId);
            return productFuture.get(30, TimeUnit.SECONDS);
        } catch (org.apache.kafka.common.errors.TimeoutException e) {
            log.error("Timeout while retrieving product details for product ID: {}", productId, e);
            throw new DatabaseAccessException(
                    "Timeout while retrieving product details",
                    e,
                    "PRODUCT_SERVICE_TIMEOUT",
                    Map.of("productId", productId)
            );
        } catch (Exception e) {
            log.error("Unexpected error while retrieving product details for product ID: {}", productId, e);
            throw new UnExpectedError(
                    "An unexpected error occurred while retrieving product details",
                    e,
                    "PRODUCT_RETRIEVAL_ERROR",
                    Map.of("productId", productId)
            );
        }
    }
}
