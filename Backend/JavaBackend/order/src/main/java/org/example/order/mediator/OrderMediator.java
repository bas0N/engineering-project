package org.example.order.mediator;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.jwtcommon.jwt.JwtCommonService;
import org.example.order.dto.Items;
import org.example.order.dto.OrderDto;
import org.example.order.dto.UserRegisterDto;
import org.example.order.dto.notify.Notify;
import org.example.order.entity.Order;
import org.example.order.entity.OrderItems;
import org.example.order.mapper.OrderMapper;
import org.example.order.service.ItemService;
import org.example.order.service.OrderService;
import org.example.order.service.ProductService;
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
    private final OrderService orderService;
    private final SignatureValidator signatureValidator;
    private final JwtCommonService jwtCommonService;
    private final ItemService itemService;
    private final ProductService productService;

    public ResponseEntity<?> createOrder(OrderDto orderDTO, HttpServletRequest request, HttpServletResponse response) {
        Order order = OrderMapper.INSTANCE.orderDtoToOrder(orderDTO);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE,"application/json");
        return ResponseEntity.status(200).headers(httpHeaders).body(orderService.createOrder(order, orderDTO.getBasketId() ,request,response));
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
                String userId = jwtCommonService.getUserFromRequest(request);
                if (userId!=null && !userId.isEmpty()){
                    List<OrderDto> orderDTOList = new ArrayList<>();
                    orderService.getOrdersByClient(userId).forEach(value->{
                        orderDTOList.add(OrderMapper.INSTANCE.toOrderDto(value));
                    });
                    return ResponseEntity.ok(orderDTOList);
                }
                //throw new OrderDontExistException();
            }catch (NullPointerException e){
                //throw new UserDontLoginException();
                throw new RuntimeException("User is not logged in");
            }
        }
        Order order = orderService.getOrderByUuid(uuid);
        List<OrderItems> itemsList = itemService.getByOrder(order);
        if (itemsList.isEmpty()){
            throw new RuntimeException("Order is empty");
        }
        List<Items> itemsDTO = new ArrayList<>();
        AtomicReference<Double> summary = new AtomicReference<>(0d);
        List<String> productIds = new ArrayList<>();
        itemsList.forEach(value->{
            Items items = OrderMapper.INSTANCE.toItems(value);
            //items.setImageUrl(productService.getProduct(value.getProduct()).getImageUrls()[0]);
            productIds.add(value.getProduct());
            itemsDTO.add(items);
            summary.set(summary.get()+value.getPriceSummary());

        });



        OrderDTO orderDTO = orderToOrderDTO.toOrderDTO(order);
        summary.set(summary.get() + orderDTO.getDeliver().getPrice());
        orderDTO.setSummaryPrice(summary.get());
        orderDTO.setItems(itemsDTO);
        return ResponseEntity.ok(orderDTO);
        return null;
    }
}
