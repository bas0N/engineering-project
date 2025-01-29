package org.example.auth.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auth.service.CloudinaryService;
import org.example.auth.service.ImageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl implements ImageService {
    private final CloudinaryService cloudinaryService;

    @Override
    public String uploadImage(MultipartFile imageFile) throws IOException {
        if (imageFile == null) {
            log.warn("Image file is null, cannot upload");
            return null;
        }

        try {
            File convertedFile = convertMultiPartToFile(imageFile);
            String imageUrl = cloudinaryService.uploadImage(convertedFile);
            log.info("Image uploaded successfully: {}", imageUrl);
            return imageUrl;
        } catch (IOException e) {
            log.error("Error while uploading image", e);
            throw e;
        }
    }

    @Override
    public void deleteImage(String imageUrl) throws Exception {
        try {
            String publicId = extractPublicIdFromUrl(imageUrl);
            cloudinaryService.deleteImage(publicId);
            log.info("Image deleted successfully with publicId: {}", publicId);
        } catch (Exception e) {
            log.error("Error while deleting image with URL: {}", imageUrl, e);
            throw e;
        }
    }

    private File convertMultiPartToFile(MultipartFile multipartFile) throws IOException {
        try {
            Path tempDir = Files.createTempDirectory("temp_files");
            File tempFile = tempDir.resolve(Objects.requireNonNull(multipartFile.getOriginalFilename())).toFile();
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(multipartFile.getBytes());
            }
            log.info("File converted successfully for upload: {}", tempFile.getAbsolutePath());
            return tempFile;
        } catch (IOException e) {
            log.error("Error while converting MultipartFile to File", e);
            throw e;
        }
    }

    private String extractPublicIdFromUrl(String url) {
        try {
            String[] urlParts = url.split("/");
            String filename = urlParts[urlParts.length - 1];
            String publicId = filename.substring(0, filename.lastIndexOf('.'));
            log.info("Extracted public ID: {}", publicId);
            return publicId;
        } catch (Exception e) {
            log.error("Error while extracting public ID from URL: {}", url, e);
            throw new IllegalArgumentException("Invalid URL format", e);
        }
    }
}
