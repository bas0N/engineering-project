package org.example.order.service;

import org.example.order.dto.request.DeliverRequest;

import java.util.List;

public interface DeliverService {
    List<DeliverRequest> getAllDeliver();
}
