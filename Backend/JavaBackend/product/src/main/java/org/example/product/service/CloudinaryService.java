package org.example.product.service;

import com.cloudinary.utils.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public interface CloudinaryService {
    String uploadImage(File file) throws IOException;
    void deleteImage(String publicId) throws IOException;

}
