package org.example.order.service.impl;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.example.order.dto.request.DeliverRequest;
import org.example.order.dto.response.DeliverResponse;
import org.example.order.entity.Deliver;
import org.example.order.mapper.DeliverMapper;
import org.example.order.repository.DeliverRepository;
import org.example.order.service.DeliverService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliverServiceImpl implements DeliverService {
    private final DeliverRepository deliverRepository;

    @Override
    public ResponseEntity<?> getAllDeliver() {
        List<DeliverResponse> deliverResponses = deliverRepository.findAll().stream().map(DeliverMapper.INSTANCE::toDeliverResponse).toList();
        return ResponseEntity.ok(deliverResponses);
    }

    @Override
    public ResponseEntity<?> createDeliverOrder(DeliverRequest deliverRequest) {
        Deliver deliver = DeliverMapper.INSTANCE.toDeliver(deliverRequest, UUID.randomUUID().toString());
        Deliver savedDeliver = deliverRepository.saveAndFlush(deliver);
        DeliverResponse deliverResponse = DeliverMapper.INSTANCE.toDeliverResponse(savedDeliver);
        return ResponseEntity.ok(deliverResponse);
    }
}
