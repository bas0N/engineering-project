package org.example.auth.kafka;

import lombok.RequiredArgsConstructor;
import org.example.commondto.UserDetailInfoEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserEventProducer {
    private static final String USER_TOPIC = "user-response-topic";

    private final KafkaTemplate<String, UserDetailInfoEvent> userKafkaTemplate;

    public void sendUserEvent(UserDetailInfoEvent userDetailInfoEvent) {
        userKafkaTemplate.send(USER_TOPIC, userDetailInfoEvent);
    }
}
