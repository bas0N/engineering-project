package org.example.product.kafka.basket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.commondto.BasketProductEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasketEventProducer {
    private static final String BASKET_TOPIC = "basket-product-response-topic";

    private final KafkaTemplate<String, BasketProductEvent> basketKafkaTemplate;

    public void sendBasketEvent(BasketProductEvent event) {
        log.info("Producing basket event: {}", event);
        basketKafkaTemplate.send(BASKET_TOPIC, event);
    }
}
