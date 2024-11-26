package org.example.order.service;

import org.example.order.dto.request.DeliverRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface DeliverService {
    ResponseEntity<?> getAllDeliver();

    ResponseEntity<?> createDeliverOrder(DeliverRequest deliverRequest);
}
