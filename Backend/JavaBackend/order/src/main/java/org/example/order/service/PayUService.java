package org.example.order.service;

import org.example.order.entity.Order;
import org.example.order.entity.OrderItems;
import org.example.order.payU.PayUOrder;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PayUService {
    String createOrder(Order finalOrder, List<OrderItems> items);
}
