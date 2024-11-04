package org.example.product.service;

import org.example.product.dto.Request.ImageRequest;
import org.example.product.entity.Image;

import java.util.List;

public interface ImageService {
    List<Image> uploadImages(List<ImageRequest> imagesFiles);
    void deleteImage(String imageId) throws Exception;

}
