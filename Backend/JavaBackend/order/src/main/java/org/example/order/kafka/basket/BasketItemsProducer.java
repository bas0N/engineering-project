package org.example.order.kafka.basket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasketItemsProducer {
    private static final String BASKET_TOPIC = "basketItems-request-topic";

    private final KafkaTemplate<String, String> basketItemsKafkaTemplate;

    public void sendBasketItemsEvent(String basketId) {
        log.info("Producing basket Items event: {}", basketId);
        basketItemsKafkaTemplate.send(BASKET_TOPIC, basketId);
    }
}
