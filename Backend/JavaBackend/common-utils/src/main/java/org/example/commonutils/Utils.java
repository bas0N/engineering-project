package org.example.commonutils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class Utils {
    public String extractUserIdFromRequest(HttpServletRequest request) {
        String userId = request.getHeader("userId");

        if (userId == null || userId.isEmpty()) {
            log.warn("UserId header not found or is empty in the request");
            return null;
        }

        log.info("Extracted userId from request: {}", userId);
        return userId;
    }

}
