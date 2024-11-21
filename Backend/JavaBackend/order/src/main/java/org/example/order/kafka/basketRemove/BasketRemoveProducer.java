package org.example.order.kafka.basketRemove;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.commondto.BasketRemoveEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasketRemoveProducer {
    private static final String BASKET_TOPIC = "basket-remove-request-topic";

    private final KafkaTemplate<String, BasketRemoveEvent> basketItemsKafkaTemplate;

    public void sendBasketRemoveEvent(BasketRemoveEvent basketRemoveEvent) {
        //log.info("Producing basket Items : {}", basketRemoveEvent);
        log.info("Producing basket Items : {}", basketRemoveEvent.getBasketId());
        basketItemsKafkaTemplate.send(BASKET_TOPIC, basketRemoveEvent);
    }
}
