package org.example.order.mapper;

import org.example.order.dto.BasketItemDto;
import org.example.order.entity.OrderItems;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ItemMapper {
    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", source = "uuid")
    @Mapping(target = "priceUnit", source = "price")
    @Mapping(target = "priceSummary", source = "summaryPrice")
    OrderItems BasketItemDtoToOrderItems(BasketItemDto basketItemDTO);
    //Item itemDtoToItem(ItemDto itemDto);
    //ItemDto itemToItemDto(Item item);
}
