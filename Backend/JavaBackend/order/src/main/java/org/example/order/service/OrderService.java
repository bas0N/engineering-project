package org.example.order.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.order.dto.notify.Notify;
import org.example.order.entity.Order;

import java.util.List;

public interface OrderService {
    String createOrder(Order order, String basketId, HttpServletRequest request, HttpServletResponse response);

    void completeOrder(Notify notify);

    List<Order> getOrdersByClient(String login);

    Order getOrderByUuid(String uuid);
}
