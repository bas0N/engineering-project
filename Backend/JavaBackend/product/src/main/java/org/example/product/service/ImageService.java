package org.example.product.service;

import org.example.product.dto.Request.ImageRequest;
import org.example.product.dto.Request.ImageReviewRequest;
import org.example.product.dto.Response.ImageUploadResponse;
import org.example.product.entity.Image;
import org.example.product.entity.ImageReview;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {
    List<Image> uploadImages(List<ImageRequest> imagesFiles);
    void deleteImage(String imageId) throws Exception;
    List<ImageReview> uploadReviewImages(List<ImageReviewRequest> imagesFiles);

    ImageUploadResponse addImage(String productId, MultipartFile hi_Res, MultipartFile large, MultipartFile thumb, String variant);



}
