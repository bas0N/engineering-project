package org.example.like.mapper;

import org.example.commondto.ImageEvent;
import org.example.like.dto.ImageResponse;
import org.example.like.entity.Image;
import org.example.like.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ImageMapper {
    ImageMapper INSTANCE = Mappers.getMapper(ImageMapper.class);

    @Mapping(target = "product", source = "product")
    @Mapping(target = "id", ignore = true)
    Image toImage(ImageEvent imageEvent, Product product);

    ImageResponse toImageResponse(Image image);
}
