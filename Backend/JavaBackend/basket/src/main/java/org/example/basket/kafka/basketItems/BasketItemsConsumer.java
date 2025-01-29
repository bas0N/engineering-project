package org.example.basket.kafka.basketItems;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.basket.service.BasketService;
import org.example.commondto.ListBasketItemEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasketItemsConsumer {
    private final BasketService basketService;
    private final BasketItemsProducer basketItemsProducer;

    @KafkaListener(topics = "basket-items-request-topic", groupId = "basket-items-service-group", containerFactory = "BasketItemsKafkaListenerContainerFactory")
    public void consumeProductResponse(@Payload String basketId, Acknowledgment ack) {
        try {
            log.info("Received basket items request for basketId {}", basketId);
            ListBasketItemEvent listBasketItemEvent = basketService.getBasketItems(basketId);
            basketItemsProducer.sendBasketEvent(listBasketItemEvent);
        } finally {
            ack.acknowledge();
        }
    }
}
