package org.example.order.service.impl;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.commondto.UserDetailInfoEvent;
import org.example.jwtcommon.jwt.JwtCommonService;
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
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final DeliverRepository deliverRepository;
    private final JwtCommonService jwtCommonService;
    private final BasketService basketService;
    private final ItemService itemService;
    private final UserService userService;
    private final ItemRepository itemRepository;

    private Order save(ListBasketItemDto basketItems, UserDetailInfoEvent userDetailInfoEvent, OrderRequest orderRequest) {
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
        String userId = jwtCommonService.getTokenFromRequest(request);
        if(userId == null || userId.isEmpty()) {
            throw new RuntimeException();
        }
        if(order.getBasketId() == null || order.getBasketId().isEmpty()) {
            throw new RuntimeException();
        }
        ListBasketItemDto basketItems = basketService.getBasket(order.getBasketId());
        UserDetailInfoEvent userDetailInfoEvent = userService.getUserInfo(userId);

        Order finalOrder = save(basketItems, userDetailInfoEvent, order);
        AtomicReference<String> result = new AtomicReference<>();
        if(order.getBasketId() != null && !order.getBasketId().isEmpty()) {
            //List<OrderItems> items = new ArrayList<>();
            basketItems.getBasketProducts().forEach(item->{
                OrderItems orderItems = ItemMapper.INSTANCE.toToOrderItems(item);
                orderItems.setOrder(finalOrder);
                orderItems.setUuid(UUID.randomUUID().toString());
               // items.add(itemService.save(orderItems));
            });
            return OrderMapper.INSTANCE.toOrderResponse(finalOrder);
        }
        else{
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
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount((long) (orderResponse.getSummaryPrice() * 100))
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
