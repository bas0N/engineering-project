package org.example.order.kafka.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.commondto.UserDetailInfoEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserConsumer {
    private final ConcurrentHashMap<String, CompletableFuture<UserDetailInfoEvent>> userFutures = new ConcurrentHashMap<>();

    @KafkaListener(
            topics = "user-response-topic",
            groupId = "product-service-user-group",
            containerFactory = "userKafkaListenerContainerFactory"
    )
    public void consumeUserResponse(@Payload UserDetailInfoEvent userDetailInfoEvent, Acknowledgment ack) {
        try {
            log.info("Consumed user response: {}", userDetailInfoEvent);
            CompletableFuture<UserDetailInfoEvent> future = userFutures.remove(userDetailInfoEvent.getUserId());
            if (future != null) {
                future.complete(userDetailInfoEvent);
            } else {
                log.warn("Received user response for userId {} but no matching request was found. Possible timeout.", userDetailInfoEvent.getUserId());
            }
        } finally {
            ack.acknowledge();
        }
    }

    public CompletableFuture<UserDetailInfoEvent> getUserDetails(String userId) {
        CompletableFuture<UserDetailInfoEvent> future = new CompletableFuture<>();
        userFutures.put(userId, future);

        future.orTimeout(30, TimeUnit.SECONDS).exceptionally(ex -> {
            userFutures.remove(userId);
            log.error("Timeout while waiting for user details for userId {}", userId);
            return null;
        });

        return future;
    }
}
