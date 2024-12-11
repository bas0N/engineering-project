package org.example.order.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.commondto.ListBasketItemEvent;
import org.example.exception.exceptions.ApiRequestException;
import org.example.order.dto.ListBasketItemDto;
import org.example.order.entity.OrderItems;
import org.example.order.kafka.basket.BasketItemsConsumer;
import org.example.order.kafka.basket.BasketItemsProducer;
import org.example.order.kafka.basketRemove.BasketRemoveProducer;
import org.example.order.mapper.ListBasketItemsMapper;
import org.example.order.service.BasketService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasketServiceImpl implements BasketService {
    private final BasketItemsProducer basketItemsEventProducer;
    private  final BasketItemsConsumer basketItemsEventConsumer;
    private final BasketRemoveProducer basketRemoveProducer;
    private final ListBasketItemsMapper listBasketItemsMapper = ListBasketItemsMapper.INSTANCE;
    public ListBasketItemDto getBasket(String basketId) {
        basketItemsEventProducer.sendBasketItemsEvent(basketId);
        CompletableFuture<ListBasketItemEvent> basketItemsFuture = basketItemsEventConsumer.getListBasketItemsDetails(basketId)
                .orTimeout(30, TimeUnit.SECONDS)
                .exceptionally(ex -> {
                    log.error("Failed to retrieve basket details for basketId: {}", basketId, ex);
                    throw new ApiRequestException("Could not retrieve basket details");
                });

        ListBasketItemEvent basketInfo = basketItemsFuture.join();
        return listBasketItemsMapper.toListBasketItemDto(basketInfo);
    }

    public void removeBasket(List<OrderItems> items, String basketId) {
        basketRemoveProducer.sendBasketRemoveEvent(basketId);
    }
}
