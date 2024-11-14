package org.example.product.mapper;

import org.example.product.dto.Response.ProductResponse;
import org.example.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);
    @Mapping(target = "owner", source = "user")
    ProductResponse toProductResponse(Product product);

    Product toProduct(ProductResponse productResponse);

}
