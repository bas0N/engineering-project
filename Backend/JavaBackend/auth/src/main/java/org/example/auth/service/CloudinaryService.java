package org.example.auth.service;

import java.io.File;
import java.io.IOException;

public interface CloudinaryService {
    String uploadImage(File file) throws IOException;
    void deleteImage(String publicId) throws IOException;
}
