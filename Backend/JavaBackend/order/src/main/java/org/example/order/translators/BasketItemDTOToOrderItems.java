package org.example.order.translators;

import org.example.order.dto.BasketItemDto;
import org.example.order.entity.OrderItems;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
public abstract class BasketItemDTOToOrderItems {


    public OrderItems toOrderItems(BasketItemDto basketItemDTO){
        return translate(basketItemDTO);
    }


    @Mappings({
            @Mapping(target = "id",ignore = true),
            @Mapping(target = "product",source = "uuid"),
            @Mapping(target = "priceUnit",source = "price"),
            @Mapping(target = "priceSummary",source = "summaryPrice")
    })
    protected abstract OrderItems translate(BasketItemDto basketItemDTO);
}
