package org.example.order.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.commonutils.Utils;
import org.example.exception.exceptions.ApiRequestException;
import org.example.order.dto.request.UpdateStatusRequest;
import org.example.order.dto.response.ItemResponse;
import org.example.order.dto.request.OrderRequest;
import org.example.order.dto.response.OrderResponse;
import org.example.order.entity.Deliver;
import org.example.order.entity.Order;
import org.example.order.entity.OrderItems;
import org.example.order.enums.Status;
import org.example.order.mapper.ItemMapper;
import org.example.order.mapper.OrderMapper;
import org.example.order.repository.DeliverRepository;
import org.example.order.repository.ItemRepository;
import org.example.order.repository.OrderRepository;
import org.example.order.service.OrderProccessingService;
import org.example.order.service.OrderService;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

@Component
@RequiredArgsConstructor
public class OrderProccessingServiceImpl implements OrderProccessingService {
    private final OrderService orderService;
    private final Utils utils;
    private final OrderRepository orderRepository;
    @Value("${stripe.endpoint.secret}")
    private String endpointSecret;
    private final ItemRepository itemRepository;
    private final DeliverRepository deliverRepository;

    public ResponseEntity<?> createOrder(OrderRequest orderRequest, HttpServletRequest request, HttpServletResponse response) {
        OrderResponse orderResponse = orderService.createOrder(orderRequest, request, response);
        String clientSecret = orderService.createStripePayment(orderResponse);
        orderResponse.setClientSecret(clientSecret);
        return ResponseEntity.ok(orderResponse);
    }

    public ResponseEntity<?> getOrderById(String uuid, HttpServletRequest request) {
        Order order = orderRepository.findByUuid(uuid).orElseThrow(() -> new ApiRequestException("Order not found with uuid: " + uuid));
        List<OrderItems> itemsList = itemRepository.findByOrder(order.getId());
        if (itemsList.isEmpty()) {
            throw new RuntimeException("Order is empty for uuid: " + uuid);
        }
        AtomicReference<Double> summary = new AtomicReference<>(0d);
        itemsList.forEach(value -> {
            ItemResponse itemResponse = ItemMapper.INSTANCE.toItemResponse(value);
            summary.set(summary.get() + value.getPriceSummary());
        });
        OrderResponse orderResponse = OrderMapper.INSTANCE.toOrderResponse(order);
        orderResponse.setSummaryPrice(summary.get());
        return ResponseEntity.ok(orderResponse);
    }

    public ResponseEntity<?> getOrdersByClient(HttpServletRequest request) {
        String userId = utils.extractUserIdFromRequest(request);
        if (userId == null || userId.isEmpty()) {
            throw new ApiRequestException("User not found");
        }
        List<Order> orders = orderRepository.findByClientWithItems(userId);
        List<OrderResponse> orderResponseList = orders.stream()
                .map(order -> {
                    List<OrderItems> itemsList = itemRepository.findByOrder(order.getId());
                    AtomicReference<Double> summary = new AtomicReference<>(0d);

                    itemsList.forEach(item -> summary.set(summary.get() + item.getPriceSummary()));

                    OrderResponse orderResponse = OrderMapper.INSTANCE.toOrderResponse(order);
                    Double deliveryPrice = deliverRepository.findByUuid(order.getDeliver().getUuid()).map(Deliver::getPrice).orElse(0d);
                    orderResponse.setSummaryPrice(summary.get() + deliveryPrice);

                    return orderResponse;
                })
                .toList();
        return ResponseEntity.ok(orderResponseList);
    }

    public ResponseEntity<?> updateStatus(UpdateStatusRequest updateStatusRequest, HttpServletRequest request) {
        Order order = orderRepository.findByUuid(updateStatusRequest.getOrderId()).orElseThrow(() -> new ApiRequestException("Order not found with uuid: " + updateStatusRequest.getOrderId()));
        orderService.updateOrderStatus(order.getUuid(), updateStatusRequest.getStatus());
        return ResponseEntity.ok().build();
    }
}
