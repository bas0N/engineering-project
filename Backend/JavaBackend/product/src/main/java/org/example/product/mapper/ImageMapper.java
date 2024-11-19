package org.example.product.mapper;

import org.example.commondto.ImageEvent;
import org.example.product.entity.Image;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ImageMapper {
    ImageMapper INSTANCE = Mappers.getMapper(ImageMapper.class);

    ImageEvent toImageEvent(Image image);

    List<ImageEvent> toImageEventList(List<Image> images);
}
