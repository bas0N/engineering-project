package org.example.order.service;

import org.example.order.dto.ListBasketItemDto;
import org.example.order.entity.OrderItems;

import java.util.List;

public interface BasketService {
    void removeBasket(List<OrderItems> items, String basketId);

    ListBasketItemDto getBasket(String basketId);
}
