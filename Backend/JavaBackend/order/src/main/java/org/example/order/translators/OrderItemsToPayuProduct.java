package org.example.order.translators;

import org.example.order.entity.OrderItems;
import org.example.order.payU.PayUProduct;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
public abstract class OrderItemsToPayuProduct {
    public PayUProduct toPayUProduct(OrderItems orderItems){
        return translate(orderItems);
    }


    @Mappings({
            @Mapping(source = "priceUnit",target = "unitPrice")
    })
    protected abstract PayUProduct translate(OrderItems orderItems);
}
