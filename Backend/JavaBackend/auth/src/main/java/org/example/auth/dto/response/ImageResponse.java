package org.example.auth.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ImageResponse {
    private final String imageUrl;
    private final String message;
}
