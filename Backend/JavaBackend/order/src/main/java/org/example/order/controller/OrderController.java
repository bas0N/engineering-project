package org.example.order.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.order.dto.request.OrderRequest;
import org.example.order.dto.request.UpdateStatusRequest;
import org.example.order.service.impl.OrderProccessingServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderProccessingServiceImpl orderProccessingServiceImpl;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest order, HttpServletRequest request, HttpServletResponse response) {
        return orderProccessingServiceImpl.createOrder(order, request, response);
    }

    @RequestMapping(path = "/{orderId}", method = RequestMethod.GET)
    public ResponseEntity<?> getOrderById(@PathVariable String orderId, HttpServletRequest request) {
        return orderProccessingServiceImpl.getOrderById(orderId, request);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getOrdersByClient(HttpServletRequest request) {
        return orderProccessingServiceImpl.getOrdersByClient(request);
    }

    @RequestMapping(path = "/notify", method = RequestMethod.POST)
    public ResponseEntity<?> notify(@RequestBody UpdateStatusRequest updateStatusRequest, HttpServletRequest request) {
        return orderProccessingServiceImpl.updateStatus(updateStatusRequest, request);
    }

}
