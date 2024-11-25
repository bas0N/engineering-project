package org.example.order.mapper;

import org.example.order.dto.response.ItemResponse;
import org.example.order.dto.response.OrderResponse;
import org.example.order.entity.Order;
import org.example.order.entity.OrderItems;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);
//    @Mapping(target = "state", source = "getAddressRequest.state")
//    @Mapping(target = "country", source = "getAddressRequest.country")
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "client", ignore = true)
//    @Mapping(source = "customerDetails.firstName", target = "firstName")
//    @Mapping(source = "customerDetails.lastName", target = "lastName")
//    @Mapping(source = "customerDetails.phone", target = "phone")
//    @Mapping(source = "customerDetails.email", target = "email")
//    @Mapping(source = "getAddressRequest.city", target = "city")
//    @Mapping(source = "getAddressRequest.street", target = "street")
//    @Mapping(source = "getAddressRequest.number", target = "number")
//    @Mapping(source = "getAddressRequest.postCode", target = "postCode")
//    @Mapping(source = "deliver", target = "deliver")
//    Order orderDtoToOrder(OrderDto orderDto);

    @Mapping(target = "clientSecret", ignore = true)
    @Mapping(target = "summaryPrice", ignore = true)
    @Mapping(target = "items", source = "orderItems")
    @Mapping(target = "customerDetails.firstName", source = "firstName")
    @Mapping(target = "customerDetails.lastName", source = "lastName")
    @Mapping(target = "customerDetails.phone", source = "phone")
    @Mapping(target = "customerDetails.email", source = "email")
    @Mapping(target = "basketId", ignore = true)
    @Mapping(target = "address.city", source = "city")
    @Mapping(target = "address.street", source = "street")
    @Mapping(target = "address.state", source = "state")
    @Mapping(target = "address.postalCode", source = "postCode")
    @Mapping(target = "address.country", source = "country")
    OrderResponse toOrderResponse(Order order);



    @Mapping(target = "summaryPrice", ignore = true)
    @Mapping(target = "price", source = "priceUnit")
    @Mapping(target = "imageUrl", ignore = true)
    ItemResponse toItems(OrderItems orderItems);
}
