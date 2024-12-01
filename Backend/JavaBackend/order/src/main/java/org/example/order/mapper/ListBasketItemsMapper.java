package org.example.order.mapper;

import org.example.commondto.BasketItemEvent;
import org.example.commondto.ListBasketItemEvent;
import org.example.order.dto.BasketItemDto;
import org.example.order.dto.ListBasketItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ListBasketItemsMapper {
    ListBasketItemsMapper INSTANCE = Mappers.getMapper(ListBasketItemsMapper.class);

    BasketItemDto toBasketItemDto(BasketItemEvent event);

    ListBasketItemDto toListBasketItemDto(ListBasketItemEvent event);


    List<BasketItemDto> toBasketItemDtos(List<BasketItemEvent> events);
}
