package org.example.order.mapper;

import org.example.order.dto.request.DeliverRequest;
import org.example.order.dto.response.DeliverResponse;
import org.example.order.entity.Deliver;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DeliverMapper {
    DeliverMapper INSTANCE = Mappers.getMapper(DeliverMapper.class);

    DeliverResponse toDeliverResponse(Deliver deliver);



    @Mapping(target = "id", ignore=true)
    @Mapping(target = "uuid", source = "uuid")
    Deliver toDeliver(DeliverRequest deliverRequest, String uuid);
}
