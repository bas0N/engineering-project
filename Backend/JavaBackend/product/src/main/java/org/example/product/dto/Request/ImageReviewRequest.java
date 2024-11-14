package org.example.product.dto.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
public class ImageReviewRequest {
    private MultipartFile small_image_file;
}
