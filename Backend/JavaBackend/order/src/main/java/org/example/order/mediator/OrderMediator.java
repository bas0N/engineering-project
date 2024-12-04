package org.example.order.mediator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.exception.exceptions.ApiRequestException;
import org.example.jwtcommon.jwt.Utils;
import org.example.order.dto.response.ItemResponse;
import org.example.order.dto.request.OrderRequest;
import org.example.order.dto.response.OrderResponse;
import org.example.order.entity.Order;
import org.example.order.entity.OrderItems;
import org.example.order.enums.Status;
import org.example.order.mapper.ItemMapper;
import org.example.order.mapper.OrderMapper;
import org.example.order.repository.ItemRepository;
import org.example.order.repository.OrderRepository;
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
public class OrderMediator {
    private final OrderService orderService;
    private final Utils utils;
    private final OrderRepository orderRepository;
    @Value("${stripe.endpoint.secret}")
    private String endpointSecret;
    private final ItemRepository itemRepository;

    public ResponseEntity<?> createOrder(OrderRequest orderRequest, HttpServletRequest request, HttpServletResponse response) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");
        OrderResponse orderResponse = orderService.createOrder(orderRequest, request, response);
        String clientSecret = orderService.createStripePayment(orderResponse);
        orderResponse.setClientSecret(clientSecret);
        return ResponseEntity.status(200).headers(httpHeaders).body(orderResponse);
    }


    public ResponseEntity<?> handleNotify(HttpServletRequest request) {
        String payload = "";
        String sigHeader = request.getHeader("Stripe-Signature");

        try (Scanner scanner = new Scanner(request.getInputStream(), "UTF-8")) {
            payload = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Event event = null;
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (Exception e) {
            // Invalid signature
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        switch (event.getType()) {
            case "payment_intent.succeeded":
                handlePaymentIntentSucceeded(event);
                break;
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.ok().build();
    }

    private void handlePaymentIntentSucceeded(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
                .getObject()
                .orElse(null);

        if (paymentIntent != null) {
            String orderId = paymentIntent.getDescription().split(":")[1].trim();
            orderService.updateOrderStatus(orderId, Status.COMPLETED);
        }
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
                    orderResponse.setSummaryPrice(summary.get());

                    return orderResponse;
                })
                .toList();

        return ResponseEntity.ok(orderResponseList);
    }
}
