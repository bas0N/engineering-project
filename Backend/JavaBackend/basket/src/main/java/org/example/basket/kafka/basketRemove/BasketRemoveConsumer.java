package org.example.basket.kafka.basketRemove;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.basket.service.BasketService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class BasketRemoveConsumer {
    private final BasketService basketService;

    @KafkaListener(topics = "basket-remove-request-topic", groupId = "basket-remove-service-group", containerFactory = "BasketRemoveKafkaListenerContainerFactory")
    public void consumeProductResponse(@Payload String basketId, Acknowledgment ack) {
        basketService.removeBasketById(basketId);
    }

}
