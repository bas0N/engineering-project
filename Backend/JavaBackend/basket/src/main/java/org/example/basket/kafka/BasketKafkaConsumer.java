package org.example.basket.kafka;

import lombok.RequiredArgsConstructor;
import org.example.basket.repository.BasketItemRepository;
import org.example.basket.repository.BasketRepository;
import org.example.commondto.BasketProductEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class BasketKafkaConsumer {
    private final ConcurrentHashMap<String, CompletableFuture<BasketProductEvent>> productFutures = new ConcurrentHashMap<>();

    @KafkaListener(topics = "basket_product_response_topic", groupId = "basket-service-group")
    public void consumeProductResponse(BasketProductEvent basketProductEvent) {
        CompletableFuture<BasketProductEvent> future = productFutures.remove(basketProductEvent.getId());
        if (future != null) {
            future.complete(basketProductEvent);
        }
    }

    public CompletableFuture<BasketProductEvent> getProductDetails(String productId) {
        CompletableFuture<BasketProductEvent> future = new CompletableFuture<>();
        productFutures.put(productId, future);
        return future;
    }
}
