package org.example.like.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.commondto.LikeEvent;
import org.example.commondto.ProductEvent;
import org.example.like.kafka.ProductInfoConsumer;
import org.example.like.kafka.ProductInfoProducer;
import org.example.like.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductInfoProducer productInfoProducer;
    private final ProductInfoConsumer productInfoConsumer;

    public ProductEvent getProduct(String productId, String userUuid) {
        productInfoProducer.sendProductEvent(new LikeEvent(userUuid, productId));
        CompletableFuture<ProductEvent> productFuture = productInfoConsumer.getProductDetails(productId)
                .orTimeout(30, TimeUnit.SECONDS);

        return productFuture.join();
    }
}
