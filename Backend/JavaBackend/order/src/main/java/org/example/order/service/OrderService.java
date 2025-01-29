package org.example.order.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.order.dto.request.OrderRequest;
import org.example.order.dto.response.OrderResponse;
import org.example.order.enums.Status;

public interface OrderService {
    OrderResponse createOrder(OrderRequest order, HttpServletRequest request, HttpServletResponse response);

    String createStripePayment(OrderResponse orderResponse);

    void updateOrderStatus(String orderId, Status status);
}
