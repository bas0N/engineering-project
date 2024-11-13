package org.example.product.kafka.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.example.commondto.BasketProductEvent;
import org.example.commondto.UserDetailInfoEvent;
import org.example.product.entity.Image;
import org.example.product.entity.Product;
import org.example.product.repository.ProductRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {
    private final ConcurrentHashMap<String, CompletableFuture<UserDetailInfoEvent>> userFutures = new ConcurrentHashMap<>();

    @KafkaListener(
            topics = "user-response-topic",
            groupId = "user-service-group",
            containerFactory = "userKafkaListenerContainerFactory"
    )
    public void consumeUserResponse(UserDetailInfoEvent userDetailInfoEvent, Acknowledgment ack) {
        CompletableFuture<UserDetailInfoEvent> future = userFutures.remove(userDetailInfoEvent.getUserId());
        if (future != null) {
            future.complete(userDetailInfoEvent);
            ack.acknowledge();
        } else {
            log.warn("Received user response for userId {} but no matching request was found. Possible timeout.", userDetailInfoEvent.getUserId());
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
