package org.example.product.kafka.basket;

import lombok.RequiredArgsConstructor;
import org.example.commondto.BasketProductEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasketEventProducer {
    private static final String BASKET_TOPIC = "basket_product_response_topic";

    private final KafkaTemplate<String, BasketProductEvent> basketKafkaTemplate;

    public void sendBasketEvent(BasketProductEvent event) {
        basketKafkaTemplate.send(BASKET_TOPIC, event);
    }
}
