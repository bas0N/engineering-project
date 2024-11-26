package org.example.order.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.order.dto.notify.Notify;
import org.example.order.dto.request.OrderRequest;
import org.example.order.dto.response.OrderResponse;
import org.example.order.entity.Order;
import org.example.order.enums.Status;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    OrderResponse createOrder(OrderRequest order, HttpServletRequest request, HttpServletResponse response);

    void completeOrder(Notify notify);

    String createStripePayment(OrderResponse orderResponse);

    void updateOrderStatus(String orderId, Status status);
}
