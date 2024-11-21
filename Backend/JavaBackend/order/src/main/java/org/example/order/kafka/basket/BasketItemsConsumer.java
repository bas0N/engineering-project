package org.example.order.kafka.basket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.commondto.ListBasketItemEvent;
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
public class BasketItemsConsumer {
    private final ConcurrentHashMap<String, CompletableFuture<ListBasketItemEvent>> basketItemsFutures = new ConcurrentHashMap<>();

    @KafkaListener(
            topics = "basketItems-response-topic",
            groupId = "order-service-basketItems-group",
            containerFactory = "basketItemsKafkaListenerContainerFactory"
    )
    public void consumeBasketItemsResponse(@Payload ListBasketItemEvent listBasketItemEvent, Acknowledgment ack) {
        try {
           log.info("Received basket items response for basketId {}", listBasketItemEvent.getBasketId());
            CompletableFuture<ListBasketItemEvent> future = basketItemsFutures.remove(listBasketItemEvent.getBasketId());
            if (future != null) {
                future.complete(listBasketItemEvent);
            } else {
                log.error("No future found for basketId {}", listBasketItemEvent.getBasketId());
            }
        } finally {
            ack.acknowledge();
        }
    }

    public CompletableFuture<ListBasketItemEvent> getListBasketItemsDetails(String basketId) {
        CompletableFuture<ListBasketItemEvent> future = new CompletableFuture<>();
        basketItemsFutures.put(basketId, future);

        future.orTimeout(30, TimeUnit.SECONDS).exceptionally(ex -> {
            basketItemsFutures.remove(basketId);
            log.error("Timeout occurred while waiting for basket items response for basketId {}", basketId);
            return null;
        });

        return future;
    }
}
