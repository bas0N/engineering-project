package org.example.product.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.commondto.UserDetailInfoEvent;
import org.example.exception.exceptions.ApiRequestException;
import org.example.product.kafka.user.UserEventConsumer;
import org.example.product.kafka.user.UserEventProducer;
import org.example.product.service.UserService;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserEventProducer userEventProducer;
    private final UserEventConsumer userEventConsumer;

    @Override
    public UserDetailInfoEvent getUserDetailInfo(String userId) {
        userEventProducer.sendUserEvent(userId);
        CompletableFuture<UserDetailInfoEvent> userFuture = userEventConsumer.getUserDetails(userId)
                .orTimeout(30, TimeUnit.SECONDS)
                .exceptionally(ex -> {
                    log.error("Failed to retrieve user details for userId: {}", userId, ex);
                    throw new ApiRequestException("Could not retrieve user details");
                });

        UserDetailInfoEvent userInfo = userFuture.join();
        log.info("User details retrieved for userId: {}", userId);
        return userInfo;
    }
}
