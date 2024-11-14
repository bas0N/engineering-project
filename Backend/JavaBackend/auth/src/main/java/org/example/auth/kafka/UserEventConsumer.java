package org.example.auth.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.example.auth.entity.User;
import org.example.auth.repository.UserRepository;
import org.example.commondto.UserDetailInfoEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class UserEventConsumer {
    private final UserRepository userRepository;
    private final UserEventProducer userEventProducer;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    @KafkaListener(
            topics = "user-request-topic",
            groupId = "user-service-group",
            containerFactory = "userKafkaListenerContainerFactory"
    )
    public void consumeUserRequest(@Payload String userId, Acknowledgment ack) {
        try {
            // Fetch the user details from the repository
            User user = userRepository.findByUuid(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

            // Create the UserDetailInfoEvent
            UserDetailInfoEvent userDetailInfoEvent = new UserDetailInfoEvent(
                    user.getUuid(), user.getEmail(), user.getFirstName(), user.getLastName()
            );

            // Send the response to the appropriate Kafka topic
            userEventProducer.sendUserEvent(userDetailInfoEvent);

        } catch (Exception e) {
            // Handle exceptions appropriately
            e.printStackTrace();
        } finally {
            // Acknowledge the message
            ack.acknowledge();
        }
    }
}


