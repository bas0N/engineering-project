package org.example.order.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.order.dto.notify.Notify;
import org.example.order.dto.request.OrderRequest;
import org.example.order.mediator.OrderMediator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderMediator orderMediator;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest order, HttpServletRequest request, HttpServletResponse response){
        return orderMediator.createOrder(order,request,response);
    }

    @RequestMapping(method = RequestMethod.POST,value = "/webhooks/stripe")
    public ResponseEntity<?> notifyOrder(HttpServletRequest request){
        return orderMediator.handleNotify(request);
    }

    @RequestMapping(path = "/{orderId}",method = RequestMethod.GET)
    public ResponseEntity<?> getOrderById(@PathVariable String orderId, HttpServletRequest request){
        return orderMediator.getOrderById(orderId, request);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getOrdersByClient(HttpServletRequest request){
        return orderMediator.getOrdersByClient(request);
    }

}
