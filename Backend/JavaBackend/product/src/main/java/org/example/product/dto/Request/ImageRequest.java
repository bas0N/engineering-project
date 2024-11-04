package org.example.product.dto.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImageRequest {
    private MultipartFile thumb;
    private MultipartFile large;
    private String variant;
    private MultipartFile hiRes;
}
