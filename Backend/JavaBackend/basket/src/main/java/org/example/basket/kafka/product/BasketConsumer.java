package org.example.basket.kafka.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.commondto.BasketProductEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasketConsumer {
    private final ConcurrentHashMap<String, CompletableFuture<BasketProductEvent>> productFutures = new ConcurrentHashMap<>();

    @KafkaListener(topics = "basket-product-response-topic", groupId = "basket-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeProductResponse(@Payload BasketProductEvent basketProductEvent, Acknowledgment ack) {
        log.info("Consumed basket product event: {}", basketProductEvent);
        CompletableFuture<BasketProductEvent> future = productFutures.remove(basketProductEvent.getId());
        if (future != null) {
            future.complete(basketProductEvent);
        }
        ack.acknowledge();
    }

    public CompletableFuture<BasketProductEvent> getProductDetails(String productId) {
        log.info("Requesting product details for product: {}", productId);
        CompletableFuture<BasketProductEvent> future = new CompletableFuture<>();
        productFutures.put(productId, future);

        future.orTimeout(30, TimeUnit.SECONDS).exceptionally(ex -> {
            productFutures.remove(productId);
            return null;
        });

        return future;
    }

}
