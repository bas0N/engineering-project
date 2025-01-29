package org.example.like.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.commondto.ProductEvent;
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
public class ProductInfoConsumer {
    private final ConcurrentHashMap<String, CompletableFuture<ProductEvent>> productFutures = new ConcurrentHashMap<>();

    @KafkaListener(
            topics = "product-details-topic",
            groupId = "like-service-group",
            containerFactory = "likeKafkaListenerContainerFactory"
    )
    public void consumeProductDetails(@Payload ProductEvent productEvent, Acknowledgment ack) {
        try {
            log.info("Consumed productEvent response: {}", productEvent);
            CompletableFuture<ProductEvent> future = productFutures.remove(productEvent.getProductId());
            if (future != null) {
                future.complete(productEvent);
            } else {
                log.warn("Received user response for userId {} but no matching request was found. Possible timeout.", productEvent.getProductId());
            }
        } finally {
            ack.acknowledge();
        }

    }

    public CompletableFuture<ProductEvent> getProductDetails(String productId) {
        CompletableFuture<ProductEvent> future = new CompletableFuture<>();
        productFutures.put(productId, future);

        future.orTimeout(30, TimeUnit.SECONDS).exceptionally(ex -> {
            productFutures.remove(productId);
            log.error("Timeout while waiting for user details for productId {}", productId);
            return null;
        });

        return future;
    }
}
