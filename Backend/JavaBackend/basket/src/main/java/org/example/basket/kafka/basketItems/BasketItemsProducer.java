package org.example.basket.kafka.basketItems;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.commondto.BasketProductEvent;
import org.example.commondto.ListBasketItemEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasketItemsProducer {
    private static final String BASKET_TOPIC = "basket-items-response-topic";

    private final KafkaTemplate<String, ListBasketItemEvent> basketKafkaTemplate;

    public void sendBasketEvent(ListBasketItemEvent event) {
        log.info("Producing basket event: {}", event);
        basketKafkaTemplate.send(BASKET_TOPIC, event);
    }
}
