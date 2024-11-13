package org.example.order.service.impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.commondto.UserDetailInfoEvent;
import org.example.jwtcommon.jwt.JwtCommonService;
import org.example.order.dto.ListBasketItemDto;
import org.example.order.dto.notify.Notify;
import org.example.order.entity.Deliver;
import org.example.order.entity.Order;
import org.example.order.entity.OrderItems;
import org.example.order.enums.Status;
import org.example.order.kafka.OrderKafkaConsumer;
import org.example.order.kafka.OrderKafkaProducer;
import org.example.order.repository.DeliverRepository;
import org.example.order.repository.OrderRepository;
import org.example.order.service.ItemService;
import org.example.order.service.OrderService;
import org.example.order.service.PayUService;
import org.example.order.translators.BasketItemDTOToOrderItems;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final DeliverRepository deliverRepository;
    private final JwtCommonService jwtCommonService;
    private final OrderKafkaProducer orderKafkaProducer;
    private final OrderKafkaConsumer orderKafkaConsumer;
    private final BasketService basketService;
//    private final ItemService itemService;
//    private final PayUService payuService;
//    private final BasketItemDTOToOrderItems basketItemDTOToItems;

    private Order save(Order order) {
        Deliver deliver = deliverRepository.findByUuid(order.getDeliver().getUuid()).orElseThrow();
        StringBuilder stringBuilder = new StringBuilder("ORDER/")
                .append(orderRepository.count())
                .append("/")
                .append(LocalDate.now().getMonthValue())
                .append("/")
                .append(LocalDate.now().getYear());

        order.setUuid(UUID.randomUUID().toString());
        order.setStatus(Status.PENDING);
        order.setOrders(stringBuilder.toString());
        order.setDeliver(deliver);
        return orderRepository.saveAndFlush(order);
    }

    @Override
    public String createOrder(Order order, HttpServletRequest request, HttpServletResponse response) {
        List<Cookie> cookies = Arrays.stream(request.getCookies()).filter(value->
                        value.getName().equals("Authorization") || value.getName().equals("refresh"))
                .toList();
        String userId = jwtCommonService.getTokenFromRequest(request);
        orderKafkaProducer.requestProductDetails(userId);
        CompletableFuture<UserDetailInfoEvent> userFuture = orderKafkaConsumer.getUserDetails(userId);
        UserDetailInfoEvent userInfo = userFuture.join();
        if (userInfo != null) {
            order.setClient(userInfo.getUserId());
        }

        Order finalOrder = save(order);
        AtomicReference<String> result = new AtomicReference<>();
        Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals("basket")).findFirst().ifPresentOrElse(value -> {
            ListBasketItemDto basket = basketService.getBasket(value);
            if (basket.getBasketProducts().isEmpty()) throw new RuntimeException();
            List<OrderItems> items = new ArrayList<>();
            basket.getBasketProducts().forEach(item -> {
                OrderItems orderItems = basketItemDTOToItems.toOrderItems(item);
                orderItems.setOrder(finalOrder);
                orderItems.setUuid(UUID.randomUUID().toString());
                items.add(itemService.save(orderItems));
                basketService.removeBasket(value,item.getUuid());
            });
            result.set(payuService.createOrder(finalOrder, items));
            value.setMaxAge(0);
            response.addCookie(value);
            //emailService.sendActivation(order.getEmail(),order.getUuid());
        }, () -> {
            throw new RuntimeException();
        });
        return result.get();
        return null;
    }

    @Override
    public void completeOrder(Notify notify) {
        Order order = orderRepository.findByUuid(notify.getOrder().getOrderId()).orElseThrow();
        order.setStatus(Status.COMPLETED);
        orderRepository.saveAndFlush(order);
    }

    @Override
    public List<Order> getOrdersByClient(String login) {
        return orderRepository.findOrderByClient(login);
    }
}
