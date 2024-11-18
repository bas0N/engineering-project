package org.example.product.dto.Response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ImageUploadResponse {
    private final String hiRes;
    private final String large;
    private final String thumb;
    private final String variant;
}
