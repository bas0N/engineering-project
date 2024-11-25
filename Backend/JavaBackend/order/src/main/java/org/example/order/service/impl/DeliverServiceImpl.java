package org.example.order.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.order.dto.request.DeliverRequest;
import org.example.order.service.DeliverService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliverServiceImpl implements DeliverService {
//    private final DeliverRepository deliverRepository;
//    private final DeliverToDeliverDto deliverToDeliverDTO;

    @Override
    public List<DeliverRequest> getAllDeliver() {
        //return deliverRepository.findAll().stream().map(deliverToDeliverDTO::deliverDto).collect(Collectors.toList());
        return null;
    }
}
