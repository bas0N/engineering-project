package org.example.order.translators;

import org.example.order.dto.Address;
import org.example.order.dto.CustomerDetails;
import org.example.order.dto.DeliverDto;
import org.example.order.dto.OrderDto;
import org.example.order.entity.Deliver;
import org.example.order.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
public abstract  class OrderToOrderDTO {
//    public OrderDto toOrderDTO(Order order){
//        return translate(order);
//    }
//
//
//    @Mappings({
//            @Mapping(target = "customerDetails",expression = "java(translateToCustomer(order))"),
//            @Mapping(target = "address",expression = "java(translateAddres(order))"),
//            @Mapping(target = "deliver",expression = "java(translateDeliver(order.getDeliver()))"),
//    })
//    protected abstract OrderDto translate(Order order);
//
//    @Mappings({})
//    protected abstract CustomerDetails translateToCustomer(Order order);
//
//    @Mappings({})
//    protected abstract Address translateAddres(Order order);
//
//    @Mappings({})
//    protected abstract DeliverDto translateDeliver(Deliver deliver);

}
