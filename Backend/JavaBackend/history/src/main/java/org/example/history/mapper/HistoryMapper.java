package org.example.history.mapper;

import org.example.commondto.ProductHistoryEvent;
import org.example.history.entity.History;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Date;

@Mapper
public interface HistoryMapper {
    HistoryMapper INSTANCE = Mappers.getMapper(HistoryMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "productHistoryEvent.productId", target = "productId")
    @Mapping(source = "productHistoryEvent.userId", target = "userId")
    History toHistory(ProductHistoryEvent productHistoryEvent);
}
