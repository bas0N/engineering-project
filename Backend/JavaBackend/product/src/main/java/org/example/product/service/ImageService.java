package org.example.product.service;

import org.example.product.dto.Response.ImageUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    ImageUploadResponse addImage(String productId, MultipartFile hi_Res, MultipartFile large, MultipartFile thumb, String variant);

}
