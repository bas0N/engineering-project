package org.example.auth.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService {
    String uploadImage(MultipartFile imageFile) throws IOException;
    void deleteImage(String imageId) throws Exception;
}
