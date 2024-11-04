package org.example.order.translators;

import org.example.order.dto.DeliverDto;
import org.example.order.entity.Deliver;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;

@Mapper
public abstract class DeliverToDeliverDto {
    public DeliverDto deliverDto(Deliver deliver){
        return translate(deliver);
    }


    @Mappings({})
    protected abstract DeliverDto translate(Deliver deliver);
}
