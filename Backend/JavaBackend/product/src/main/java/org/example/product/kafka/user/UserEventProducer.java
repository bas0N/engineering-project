package org.example.product.kafka.user;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserEventProducer {
    private static final String USER_TOPIC = "user-request-topic";

    private final KafkaTemplate<String, String> userKafkaTemplate;

    public void sendUserEvent(String userId) {
        userKafkaTemplate.send(USER_TOPIC, userId);
    }
}
