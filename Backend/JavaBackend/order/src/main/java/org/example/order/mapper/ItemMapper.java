package org.example.order.mapper;

import org.example.order.dto.BasketItemDto;
import org.example.order.dto.response.ItemResponse;
import org.example.order.entity.OrderItems;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ItemMapper {
    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    @Mapping(target = "order", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", source = "uuid")
    @Mapping(target = "priceUnit", source = "price")
    @Mapping(target = "priceSummary", expression = "java(basketItemDTO.getPrice() * basketItemDTO.getQuantity())")
    OrderItems toToOrderItems(BasketItemDto basketItemDTO);

    ItemResponse toItemResponse(OrderItems orderItems);

    List<ItemResponse> toItemResponseList(List<OrderItems> orderItems);

}
