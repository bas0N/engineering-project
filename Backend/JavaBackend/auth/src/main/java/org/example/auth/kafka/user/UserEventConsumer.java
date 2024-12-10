package org.example.auth.kafka.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.example.auth.entity.User;
import org.example.auth.repository.UserRepository;
import org.example.commondto.UserDetailInfoEvent;
import org.example.exception.exceptions.UnExpectedError;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {
    private final UserRepository userRepository;
    private final UserEventProducer userEventProducer;

    @KafkaListener(
            topics = "user-request-topic",
            groupId = "user-service-group",
            containerFactory = "userKafkaListenerContainerFactory"
    )
    public void consumeUserRequest(@Payload String userId, Acknowledgment ack) {
        try {
            log.info("Consumed user request: {}", userId);
            User user = userRepository.findUserByUuid(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

            UserDetailInfoEvent userDetailInfoEvent = user.isActive()
                    ? new UserDetailInfoEvent(
                    user.getUuid(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getPhoneNumber(),
                    true)
                    : new UserDetailInfoEvent(
                    user.getUuid(),
                    null,
                    null,
                    null,
                    null,
                    false);

            userEventProducer.sendUserEvent(userDetailInfoEvent);
            log.info("User event successfully sent for userId: {}", userId);

        } catch (ResourceNotFoundException ex) {
            log.error("User not found with id: {}. Error: {}", userId, ex.getMessage());
        } catch (Exception ex) {
            log.error("Unexpected error occurred while processing userId: {}. Error: {}", userId, ex.getMessage(), ex);
            throw new UnExpectedError("Unexpected error occurred while processing userId: " + userId);
        } finally {
            try {
                ack.acknowledge();
                log.info("Message acknowledged for userId: {}", userId);
            } catch (Exception ackEx) {
                log.error("Failed to acknowledge message for userId: {}. Error: {}", userId, ackEx.getMessage(), ackEx);
            }
        }
    }
}


