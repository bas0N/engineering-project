package org.example.product.mapper;

import org.example.commondto.ProductEvent;
import org.example.product.dto.Request.AddProductRequest;
import org.example.product.dto.Response.ProductDetailResponse;
import org.example.product.dto.Response.ProductResponse;
import org.example.product.entity.Product;
import org.example.product.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    ProductResponse toProductResponse(Product product);

    @Mapping(target = "owner", source = "user")
    ProductDetailResponse toProductDetailResponse(Product product, User user);

    @Mapping(target = "productId", source = "parentAsin")
    ProductEvent toProductEvent(Product product);

    List<ProductResponse> toProductResponseList(List<Product> products);


    @Mapping(target = "videos", ignore = true)
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "ratingNumber", constant = "0")
    @Mapping(target = "parentAsin", source = "uuid")
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "boughtTogether", ignore = true)
    @Mapping(target = "averageRating", constant = "0.0")
    Product toProduct(AddProductRequest addProductRequest, String uuid, String userId);

}
