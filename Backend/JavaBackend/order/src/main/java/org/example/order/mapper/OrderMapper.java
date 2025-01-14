package org.example.order.mapper;

import org.example.order.dto.response.OrderResponse;
import org.example.order.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(target = "orderId", source = "order.uuid")
    @Mapping(target = "clientSecret", ignore = true)
    @Mapping(target = "summaryPrice", source = "order.summaryPrice")
    @Mapping(target = "items", source = "orderItems")
    @Mapping(target = "customerDetails.firstName", source = "firstName")
    @Mapping(target = "customerDetails.lastName", source = "lastName")
    @Mapping(target = "customerDetails.phone", source = "phone")
    @Mapping(target = "customerDetails.email", source = "email")
    @Mapping(target = "address.city", source = "city")
    @Mapping(target = "address.street", source = "street")
    @Mapping(target = "address.state", source = "state")
    @Mapping(target = "address.postalCode", source = "postCode")
    @Mapping(target = "address.country", source = "country")
    OrderResponse toOrderResponse(Order order);
}
