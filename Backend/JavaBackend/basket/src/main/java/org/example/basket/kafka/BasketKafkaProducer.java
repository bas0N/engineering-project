package org.example.basket.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasketKafkaProducer {
    private static final String REQUEST_TOPIC = "basket-product-request-topic";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void requestProductDetails(String productId) {
        log.info("Requesting product details for product: {}", productId);
        kafkaTemplate.send(REQUEST_TOPIC, productId);
    }
}
