package org.example.product.mapper;

import org.example.commondto.ProductEvent;
import org.example.product.dto.Response.ProductResponse;
import org.example.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);
    @Mapping(target = "owner", source = "user")
    ProductResponse toProductResponse(Product product);

    @Mapping(target = "productId", source = "parentAsin")
    ProductEvent toProductEvent(Product product);

}
