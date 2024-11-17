package org.example.auth.service.impl;

import lombok.RequiredArgsConstructor;
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
public class ImageServiceImpl implements ImageService {
    private final CloudinaryService cloudinaryService;
    public String uploadImage(MultipartFile imageFile) throws IOException {
        if (imageFile == null) return null;
        File convertedFile = convertMultiPartToFile(imageFile);
        return cloudinaryService.uploadImage(convertedFile);
    }
    public void deleteImage(String imageUrl) throws Exception {
        cloudinaryService.deleteImage(extractPublicIdFromUrl(imageUrl));
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
