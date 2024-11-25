package org.example.order.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.order.entity.Order;
import org.example.order.entity.OrderItems;
import org.example.order.repository.ItemRepository;
import org.example.order.service.ItemService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    public OrderItems save(OrderItems items){
        return itemRepository.saveAndFlush(items);
    }

    public List<OrderItems> getByOrder(Order order) {
        return itemRepository.findByOrder(order.getId());
    }

}
