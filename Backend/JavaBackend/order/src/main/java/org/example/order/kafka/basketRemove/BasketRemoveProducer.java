package org.example.order.kafka.basketRemove;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasketRemoveProducer {
    private static final String BASKET_TOPIC = "basket-remove-request-topic";

    private final KafkaTemplate<String, String> basketItemsKafkaTemplate;

    public void sendBasketRemoveEvent(String basketId) {
        log.info("Producing basket Items : {}", basketId);
        basketItemsKafkaTemplate.send(BASKET_TOPIC, basketId);
    }
}
