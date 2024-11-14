package org.example.order.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.order.dto.OrderDto;
import org.example.order.dto.notify.Notify;
import org.example.order.mediator.OrderMediator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderMediator orderMediator;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> createOrder(@RequestBody OrderDto order, HttpServletRequest request, HttpServletResponse response){
        return orderMediator.createOrder(order,request,response);
    }

    @RequestMapping(method = RequestMethod.POST,value = "/notification")
    public ResponseEntity<?> notifyOrder(@RequestBody Notify notify, HttpServletRequest request){
        return orderMediator.handleNotify(notify,request);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> get(@RequestParam(required = false) String uuid,HttpServletRequest request){
        return orderMediator.getOrder(uuid,request);
    }
}
