package org.example.order.kafka;

import lombok.RequiredArgsConstructor;
import org.example.commondto.BasketProductEvent;
import org.example.commondto.UserDetailInfoEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class OrderKafkaConsumer {
    private final ConcurrentHashMap<String, CompletableFuture<UserDetailInfoEvent>> userFutures = new ConcurrentHashMap<>();

    @KafkaListener(topics = "order_response_topic", groupId = "order-service-group")
    public void consumeUserResponse(UserDetailInfoEvent userDetailInfoEvent) {
        CompletableFuture<UserDetailInfoEvent> future = userFutures.remove(userDetailInfoEvent.getUserId());
        if (future != null) {
            future.complete(userDetailInfoEvent);
        }
    }

    public CompletableFuture<UserDetailInfoEvent> getUserDetails(String userId) {
        CompletableFuture<UserDetailInfoEvent> future = new CompletableFuture<>();
        userFutures.put(userId, future);
        return future;
    }
}
