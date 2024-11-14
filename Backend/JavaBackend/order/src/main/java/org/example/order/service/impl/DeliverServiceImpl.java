package org.example.order.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.order.dto.DeliverDto;
import org.example.order.repository.DeliverRepository;
import org.example.order.service.DeliverService;
import org.example.order.translators.DeliverToDeliverDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliverServiceImpl implements DeliverService {
//    private final DeliverRepository deliverRepository;
//    private final DeliverToDeliverDto deliverToDeliverDTO;

    @Override
    public List<DeliverDto> getAllDeliver() {
        //return deliverRepository.findAll().stream().map(deliverToDeliverDTO::deliverDto).collect(Collectors.toList());
        return null;
    }
}
