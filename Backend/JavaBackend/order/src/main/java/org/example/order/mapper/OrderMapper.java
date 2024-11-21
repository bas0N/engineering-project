package org.example.order.mapper;

import org.example.order.dto.Items;
import org.example.order.dto.OrderDto;
import org.example.order.entity.Order;
import org.example.order.entity.OrderItems;
import org.example.order.payU.PayUProduct;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(source = "customerDetails.firstName", target = "firstName")
    @Mapping(source = "customerDetails.lastName", target = "lastName")
    @Mapping(source = "customerDetails.phone", target = "phone")
    @Mapping(source = "customerDetails.email", target = "email")
    @Mapping(source = "address.city", target = "city")
    @Mapping(source = "address.street", target = "street")
    @Mapping(source = "address.number", target = "number")
    @Mapping(source = "address.postCode", target = "postCode")
    @Mapping(source = "deliver", target = "deliver")
    Order orderDtoToOrder(OrderDto orderDto);

    @Mapping(target = "summaryPrice", ignore = true)
    @Mapping(target = "items", source = "orderItems")
    @Mapping(target = "customerDetails.firstName", source = "firstName")
    @Mapping(target = "customerDetails.lastName", source = "lastName")
    @Mapping(target = "customerDetails.phone", source = "phone")
    @Mapping(target = "customerDetails.email", source = "email")
    @Mapping(target = "basketId", ignore = true)
    @Mapping(target = "address.city", source = "city")
    @Mapping(target = "address.street", source = "street")
    @Mapping(target = "address.number", source = "number")
    @Mapping(target = "address.postCode", source = "postCode")
    OrderDto toOrderDto(Order order);

    @Mapping(target = "unitPrice", source = "priceUnit")
    PayUProduct toPayuProduct(OrderItems orderItems);

    @Mapping(target = "summaryPrice", ignore = true)
    @Mapping(target = "price", source = "priceUnit")
    @Mapping(target = "imageUrl", ignore = true)
    Items toItems(OrderItems orderItems);
}
