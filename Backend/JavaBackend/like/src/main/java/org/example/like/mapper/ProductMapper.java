package org.example.like.mapper;

import org.example.commondto.ProductEvent;
import org.example.like.dto.ProductResponse;
import org.example.like.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", source = "productId")
    @Mapping(target = "images", ignore = true)
    Product toProduct(ProductEvent productEvent);

    ProductResponse toProductResponse(Product product);

    List<ProductResponse> toProductResponseList(List<Product> products);
}
