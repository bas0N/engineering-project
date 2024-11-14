package org.example.basket.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasketKafkaProducer {
    private static final String REQUEST_TOPIC = "basket-product-request-topic";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void requestProductDetails(String productId) {

        kafkaTemplate.send(REQUEST_TOPIC, productId);
    }
}
