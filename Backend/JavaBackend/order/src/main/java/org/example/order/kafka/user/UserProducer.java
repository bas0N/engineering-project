package org.example.order.kafka.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProducer {
    private static final String USER_TOPIC = "user-request-topic";

    private final KafkaTemplate<String, String> userKafkaTemplate;

    public void sendUserEvent(String userId) {
        log.info("Producing user event: {}", userId);
        userKafkaTemplate.send(USER_TOPIC, userId);
    }
}
