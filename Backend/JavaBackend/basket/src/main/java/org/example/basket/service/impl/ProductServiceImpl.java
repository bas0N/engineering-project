package org.example.basket.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.basket.kafka.product.BasketConsumer;
import org.example.basket.kafka.product.BasketProducer;
import org.example.basket.service.ProductService;
import org.example.commondto.BasketProductEvent;
import org.example.commondto.ProductEvent;
import org.example.commondto.UserDetailInfoEvent;
import org.example.exception.exceptions.ApiRequestException;
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
        basketProducer.sendProductDetailsEvent(productId);
        CompletableFuture<BasketProductEvent> productFuture = basketConsumer.getProductDetails(productId)
                .orTimeout(30, TimeUnit.SECONDS)
                .exceptionally(ex -> {
                    log.error("Failed to retrieve product details for productId: {}", productId, ex);
                    throw new ApiRequestException("Could not retrieve user details");
                });

        BasketProductEvent basketProductInfo = productFuture.join();
        log.info("Product details retrieved for productId: {}", productId);
        return basketProductInfo;
    }
}
