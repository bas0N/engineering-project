package org.example.order.service;

import org.example.order.entity.Order;
import org.example.order.entity.OrderItems;

import java.util.List;

public interface ItemService {

    OrderItems save(OrderItems items);

    List<OrderItems> getByOrder(Order order);
}
