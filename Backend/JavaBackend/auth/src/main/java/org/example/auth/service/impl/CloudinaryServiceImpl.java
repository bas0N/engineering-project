package org.example.auth.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auth.service.CloudinaryService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryServiceImpl implements CloudinaryService {
    private final Cloudinary cloudinary;

    @Override
    public String uploadImage(File file) throws IOException {
        try {
            Map response = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
            String imageUrl = (String) response.get("url");
            log.info("Image uploaded to Cloudinary: {}", imageUrl);
            return imageUrl;
        } catch (IOException e) {
            log.error("Error while uploading image to Cloudinary", e);
            throw e;
        }
    }

    @Override
    public void deleteImage(String publicId) throws IOException {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("Image deleted from Cloudinary with public ID: {}", publicId);
        } catch (IOException e) {
            log.error("Error while deleting image from Cloudinary with public ID: {}", publicId, e);
            throw e;
        }
    }
}
