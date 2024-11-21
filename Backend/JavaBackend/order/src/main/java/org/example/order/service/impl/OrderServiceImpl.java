package org.example.order.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.jwtcommon.jwt.JwtCommonService;
import org.example.order.dto.ListBasketItemDto;
import org.example.order.dto.notify.Notify;
import org.example.order.entity.Deliver;
import org.example.order.entity.Order;
import org.example.order.entity.OrderItems;
import org.example.order.enums.Status;
import org.example.order.mapper.ItemMapper;
import org.example.order.repository.DeliverRepository;
import org.example.order.repository.OrderRepository;
import org.example.order.service.ItemService;
import org.example.order.service.OrderService;
import org.example.order.service.PayUService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final DeliverRepository deliverRepository;
    private final JwtCommonService jwtCommonService;
    private final BasketService basketService;
    private final ItemService itemService;
    private final PayUService payuService;

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
    public String createOrder(Order order, String basketId, HttpServletRequest request, HttpServletResponse response) {
        String userId = jwtCommonService.getTokenFromRequest(request);

        if (userId != null && !userId.isEmpty()) {
            order.setClient(userId);
        }

        Order finalOrder = save(order);
        AtomicReference<String> result = new AtomicReference<>();
        if(basketId != null && !basketId.isEmpty()) {
            ListBasketItemDto basket = basketService.getBasket(basketId);
            if(basket.getBasketProducts().isEmpty()) {
                throw new RuntimeException();
            }
            List<OrderItems> items = new ArrayList<>();
            basket.getBasketProducts().forEach(item -> {
                OrderItems orderItems = ItemMapper.INSTANCE.toToOrderItems(item);
                orderItems.setOrder(finalOrder);
                orderItems.setUuid(UUID.randomUUID().toString());
                items.add(itemService.save(orderItems));
            });
            basketService.removeBasket(items, basketId);
            result.set(payuService.createOrder(finalOrder, items));
        }
        else{
            throw new RuntimeException();
        }
        return result.get();
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

    @Override
    public Order getOrderByUuid(String uuid) {
        return orderRepository.findOrderByUuid(uuid).orElseThrow();
    }
}
