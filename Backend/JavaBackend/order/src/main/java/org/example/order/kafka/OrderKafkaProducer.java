package org.example.order.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderKafkaProducer {
    private static final String REQUEST_TOPIC = "order_request_topic";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void requestProductDetails(String userId) {

        kafkaTemplate.send(REQUEST_TOPIC, userId);
    }
}
