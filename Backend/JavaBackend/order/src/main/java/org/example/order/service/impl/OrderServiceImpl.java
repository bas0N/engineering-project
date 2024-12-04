package org.example.order.service.impl;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.commondto.UserDetailInfoEvent;
import org.example.jwtcommon.jwt.Utils;
import org.example.order.dto.BasketItemDto;
import org.example.order.dto.ListBasketItemDto;
import org.example.order.dto.notify.Notify;
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
import org.example.order.service.ItemService;
import org.example.order.service.OrderService;
import org.example.order.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final DeliverRepository deliverRepository;
    private final Utils utils;
    private final BasketService basketService;
    private final OrderMapper orderMapper = OrderMapper.INSTANCE;
    private final ItemMapper itemMapper = ItemMapper.INSTANCE;
    private final UserService userService;
    private final ItemRepository itemRepository;

    private Order save(UserDetailInfoEvent userDetailInfoEvent, OrderRequest orderRequest) {
        Deliver deliver = deliverRepository.findByUuid(orderRequest.getDeliverId()).orElseThrow();
        String stringBuilder = "ORDER/" +
                orderRepository.count() +
                "/" +
                LocalDate.now().getMonthValue() +
                "/" +
                LocalDate.now().getYear();
        Order order = new Order();
        setOrderUserDetails(userDetailInfoEvent, order);
        setOrderAddressDetails(orderRequest, order);
        order.setBasketId(orderRequest.getBasketId());
        order.setUuid(UUID.randomUUID().toString());
        order.setStatus(Status.PENDING);
        order.setOrders(stringBuilder);
        order.setDeliver(deliver);
        return orderRepository.saveAndFlush(order);
    }

    private static void setOrderAddressDetails(OrderRequest orderRequest, Order order) {
        order.setCity(orderRequest.getAddressRequest().getCity());
        order.setStreet(orderRequest.getAddressRequest().getStreet());
        order.setPostCode(orderRequest.getAddressRequest().getPostalCode());
        order.setState(orderRequest.getAddressRequest().getState());
        order.setCountry(orderRequest.getAddressRequest().getCountry());
    }

    private static void setOrderUserDetails(UserDetailInfoEvent userDetailInfoEvent, Order order) {
        order.setFirstName(userDetailInfoEvent.getFirstName());
        order.setLastName(userDetailInfoEvent.getLastName());
        order.setPhone(userDetailInfoEvent.getPhone());
        order.setEmail(userDetailInfoEvent.getEmail());
        order.setClient(userDetailInfoEvent.getUserId());
    }

    @Override
    public OrderResponse createOrder(OrderRequest order, HttpServletRequest request, HttpServletResponse response) {
        String userId = utils.extractUserIdFromRequest(request);
        if (userId == null || userId.isEmpty()) {
            throw new RuntimeException();
        }
        if (order.getBasketId() == null || order.getBasketId().isEmpty()) {
            throw new RuntimeException();
        }
        ListBasketItemDto basketItems = basketService.getBasket(order.getBasketId());
        UserDetailInfoEvent userDetailInfoEvent = userService.getUserInfo(userId);

        Order finalOrder = save(userDetailInfoEvent, order);
        if (order.getBasketId() != null && !order.getBasketId().isEmpty()) {
            basketItems.getBasketProducts().forEach(item -> {
                OrderItems orderItem = itemMapper.toToOrderItems(item);
                orderItem.setOrder(finalOrder);
                orderItem.setUuid(UUID.randomUUID().toString());
                finalOrder.getOrderItems().add(orderItem);
            });

            orderRepository.saveAndFlush(finalOrder);

            Order savedOrder = orderRepository.findByUuidWithItems(finalOrder.getUuid())
                    .orElseThrow(() -> new RuntimeException("Order not found after saving"));
            return orderMapper.toOrderResponse(savedOrder);
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public void completeOrder(Notify notify) {
        Order order = orderRepository.findByUuid(notify.getOrder().getOrderId()).orElseThrow();
        order.setStatus(Status.COMPLETED);
        orderRepository.saveAndFlush(order);
    }

    @Override
    public String createStripePayment(OrderResponse orderResponse) {
        try {
            double summaryPrice = orderResponse.getSummaryPrice();

            long amountInCents = summaryPrice > 0.0
                    ? (long) (summaryPrice * 100)
                    : 500;

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount((amountInCents))
                    .setCurrency("pln")
                    .putMetadata("order_id", orderResponse.getUuid())
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);

            return intent.getClientSecret();
        } catch (StripeException e) {
            throw new RuntimeException("Failed to create Stripe payment", e);
        }
    }

    @Override
    public void updateOrderStatus(String orderId, Status status) {
        Order order = orderRepository.findByUuid(orderId).orElseThrow();
        order.setStatus(status);
        orderRepository.saveAndFlush(order);
        List<OrderItems> items = itemRepository.findByOrder(order.getId());
        basketService.removeBasket(items, order.getBasketId());
    }

}
