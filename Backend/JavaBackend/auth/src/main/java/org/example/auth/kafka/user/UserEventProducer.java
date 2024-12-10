package org.example.auth.kafka.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.commondto.UserDetailInfoEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventProducer {
    private static final String USER_TOPIC = "user-response-topic";

    private final KafkaTemplate<String, UserDetailInfoEvent> userKafkaTemplate;

    public void sendUserEvent(UserDetailInfoEvent userDetailInfoEvent) {
        log.info("Producing user event: {}", userDetailInfoEvent);
        userKafkaTemplate.send(USER_TOPIC, userDetailInfoEvent);
    }
}
