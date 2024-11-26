package org.example.order.controller;

import lombok.RequiredArgsConstructor;
import org.example.order.dto.request.DeliverRequest;
import org.example.order.service.DeliverService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/order/deliver")
@RequiredArgsConstructor
public class DeliverController {
    private final DeliverService deliverService;
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getDeliver(){
        return deliverService.getAllDeliver();
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> createDeliverOrder(@RequestBody DeliverRequest deliverRequest){
        return deliverService.createDeliverOrder(deliverRequest);
    }
}
