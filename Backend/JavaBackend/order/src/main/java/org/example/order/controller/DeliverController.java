package org.example.order.controller;

import lombok.RequiredArgsConstructor;
import org.example.order.dto.request.DeliverRequest;
import org.example.order.service.DeliverService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/deliver")
@RequiredArgsConstructor
public class DeliverController {
    private final DeliverService deliverService;
    @RequestMapping(method = RequestMethod.GET)
    public List<DeliverRequest> getDeliver(){
        return deliverService.getAllDeliver();
    }
}
