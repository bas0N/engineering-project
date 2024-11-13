package org.example.product.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.product.dto.Request.ImageRequest;
import org.example.product.dto.Request.ImageReviewRequest;
import org.example.product.entity.Image;
import org.example.product.entity.ImageReview;
import org.example.product.service.ImageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final CloudinaryServiceImpl cloudinaryService;

    @Override
    public List<Image> uploadImages(List<ImageRequest> imagesFiles) {
        return imagesFiles.stream().map(imageRequest -> {
            try {
                return new Image(
                        uploadImage(imageRequest.getThumb()),
                        uploadImage(imageRequest.getLarge()),
                        imageRequest.getVariant() == null ? null : imageRequest.getVariant(),
                        uploadImage(imageRequest.getHiRes())
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

    @Override
    public void deleteImage(String imageUrl) throws Exception {
        cloudinaryService.deleteImage(extractPublicIdFromUrl(imageUrl));
    }

    @Override
    public List<ImageReview> uploadReviewImages(List<ImageReviewRequest> imagesFiles) {
        return imagesFiles.stream().map(imageRequest -> {
            try {
                return new ImageReview(uploadImage(imageRequest.getSmall_image_file()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

    private String uploadImage(MultipartFile imageFile) throws IOException {
        if (imageFile == null) return null;
        File convertedFile = convertMultiPartToFile(imageFile);
        return cloudinaryService.uploadImage(convertedFile);
    }

    private File convertMultiPartToFile(MultipartFile multipartFile) throws IOException {
        Path tempDir = Files.createTempDirectory("temp_files");
        File tempFile = tempDir.resolve(Objects.requireNonNull(multipartFile.getOriginalFilename())).toFile();
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
        }
        return tempFile;
    }

    private String extractPublicIdFromUrl(String url) {
        String[] urlParts = url.split("/");
        String filename = urlParts[urlParts.length - 1];
        String publicId = filename.substring(0, filename.lastIndexOf('.'));
        return publicId;
    }
}
