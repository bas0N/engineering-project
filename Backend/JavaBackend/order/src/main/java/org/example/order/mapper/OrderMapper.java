package org.example.order.mapper;

import org.example.order.dto.OrderDto;
import org.example.order.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);
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
}
