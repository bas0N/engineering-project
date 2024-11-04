package org.example.order.mediator;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.order.dto.OrderDto;
import org.example.order.dto.UserRegisterDto;
import org.example.order.dto.notify.Notify;
import org.example.order.entity.Order;
import org.example.order.service.OrderService;
import org.example.order.translators.OrderDTOToOrder;
import org.example.order.translators.OrderToOrderDTO;
import org.example.order.validator.SignatureValidator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Component
@RequiredArgsConstructor
public class OrderMediator {
    private final OrderDTOToOrder orderDTOToOrder;
    private final OrderService orderService;
    private final SignatureValidator signatureValidator;
    private final OrderToOrderDTO orderToOrderDTO;

    public ResponseEntity<?> createOrder(OrderDto orderDTO, HttpServletRequest request, HttpServletResponse response) {
        Order order = orderDTOToOrder.toOrder(orderDTO);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE,"application/json");
        return ResponseEntity.status(200).headers(httpHeaders).body(orderService.createOrder(order,request,response));
    }


    public ResponseEntity<?> handleNotify(Notify notify, HttpServletRequest request) {
        String header = request.getHeader("OpenPayu-Signature");
        try {
            signatureValidator.validate(header,notify);
            orderService.completeOrder(notify);
        } catch (NoSuchAlgorithmException | JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Bad signature");
        }
//        }catch (OrderDontExistException e1){
//            return ResponseEntity.badRequest().body(new Response("Order don't exist"));
//        };
        return ResponseEntity.ok("Notification handle success");
    }

    public ResponseEntity<?> getOrder(String uuid, HttpServletRequest request) {
        if (uuid == null || uuid.isEmpty()){
            try{
                List<Cookie> cookies = Arrays.stream(request.getCookies()).filter(value->
                                value.getName().equals("Authorization") || value.getName().equals("refresh"))
                        .toList();
                //UserRegisterDTO user = authService.getUserDetails(cookies);
                UserRegisterDto user = new UserRegisterDto();
                if (user!=null){
                    List<OrderDto> orderDTOList = new ArrayList<>();
                    orderService.getOrdersByClient(user.getLogin()).forEach(value->{
                        orderDTOList.add(orderToOrderDto.toOrderDTO(value));
                    });
                    return ResponseEntity.ok(orderDTOList);
                }
                //throw new OrderDontExistException();
            }catch (NullPointerException e){
                //throw new UserDontLoginException();
            }
        }
        Order order = orderService.getOrderByUuid(uuid);
        List<OrderItems> itemsList = itemService.getByOrder(order);
        if (itemsList.isEmpty()) throw new OrderDontExistException();
        List<Items> itemsDTO = new ArrayList<>();
        AtomicReference<Double> summary = new AtomicReference<>(0d);
        itemsList.forEach(value->{
            Items items = orderItemsToItems.toItems(value);
            items.setImageUrl(productService.getProduct(value.getProduct()).getImageUrls()[0]);
            itemsDTO.add(items);
            summary.set(summary.get()+value.getPriceSummary());

        });
        OrderDTO orderDTO = orderToOrderDTO.toOrderDTO(order);
        summary.set(summary.get() + orderDTO.getDeliver().getPrice());
        orderDTO.setSummaryPrice(summary.get());
        orderDTO.setItems(itemsDTO);
        return ResponseEntity.ok(orderDTO);
    }
}
