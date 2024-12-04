package org.example.auth.kafka.userDeActive;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDeActiveProducer {
    private static final String USER_TOPIC = "user-deactive-request-topic";

    private final KafkaTemplate<String, String> userDeactiveKafkaTemplate;

    public void sendUserEvent(String userUuid) {
        log.info("Producing user Uuid event: {}", userUuid);
        userDeactiveKafkaTemplate.send(USER_TOPIC, userUuid);
    }
}
