package org.example.order.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.commondto.ListBasketItemEvent;
import org.example.commondto.UserDetailInfoEvent;
import org.example.exception.exceptions.ApiRequestException;
import org.example.order.dto.ListBasketItemDto;
import org.example.order.kafka.user.UserConsumer;
import org.example.order.kafka.user.UserProducer;
import org.example.order.mapper.ListBasketItemsMapper;
import org.example.order.service.UserService;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserProducer userProducer;
    private final UserConsumer userConsumer;

    public UserDetailInfoEvent getUserInfo(String userId) {
        userProducer.sendUserEvent(userId);
        CompletableFuture<UserDetailInfoEvent> userInfoFuture = userConsumer.getUserDetails(userId)
                .orTimeout(30, TimeUnit.SECONDS)
                .exceptionally(ex -> {
                    //log.error("Failed to retrieve basket details for basketId: {}", basketId, ex);
                    log.error("Failed to retrieve user details for userId: {}", userId, ex);
                    throw new ApiRequestException("Could not retrieve user details");
                });

        return userInfoFuture.join();
    }
}
