package org.example.product.mapper;

import org.example.product.dto.Response.ProductResponse;
import org.example.product.entity.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductResponse toProductResponse(Product product);

    Product toProduct(ProductResponse productResponse);
}
