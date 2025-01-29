package org.example.order.service;

import com.stripe.model.Event;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.order.dto.request.OrderRequest;
import org.example.order.dto.request.UpdateStatusRequest;
import org.springframework.http.ResponseEntity;

public interface OrderProccessingService {
    ResponseEntity<?> createOrder(OrderRequest orderRequest, HttpServletRequest request, HttpServletResponse response);

    ResponseEntity<?> getOrderById(String uuid, HttpServletRequest request);
    ResponseEntity<?> getOrdersByClient(HttpServletRequest request);

    ResponseEntity<?> updateStatus(UpdateStatusRequest updateStatusRequest, HttpServletRequest request);
}
