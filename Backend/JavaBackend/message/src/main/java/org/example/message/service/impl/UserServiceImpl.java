package org.example.message.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.commondto.UserDetailInfoEvent;
import org.example.exception.exceptions.ApiRequestException;
import org.example.message.kafka.UserConsumer;
import org.example.message.kafka.UserProducer;
import org.example.message.service.UserService;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserProducer userProducer;
    private final UserConsumer userConsumer;
    @Override
    public UserDetailInfoEvent getUserInfo(String userId) {
        userProducer.sendUserEvent(userId);
        CompletableFuture<UserDetailInfoEvent> userInfoFuture = userConsumer.getUserDetails(userId)
                .orTimeout(30, TimeUnit.SECONDS)
                .exceptionally(ex -> {
                    log.error("Failed to retrieve user details for userId: {}", userId, ex);
                    throw new ApiRequestException("Could not retrieve user details");
                });

        return userInfoFuture.join();
    }
}
