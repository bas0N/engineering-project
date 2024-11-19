package org.example.product.kafka.like;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.commondto.ProductEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeEventProducer {
    private static final String BASKET_TOPIC = "product-details-topic";

    private final KafkaTemplate<String, ProductEvent> basketKafkaTemplate;

    public void sendProductEvent(ProductEvent event) {
        log.info("Producing basket event: {}", event);
        basketKafkaTemplate.send(BASKET_TOPIC, event);
    }
}
