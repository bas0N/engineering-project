package org.example.user.service;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auth.entity.User;
import org.example.auth.repository.UserRepository;
import org.example.auth.service.JwtService;
import org.example.user.dto.UserDetailsDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserInfoService {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public ResponseEntity<?> getUserInfo(HttpServletRequest request) {
        User user = getCurrentUser(request);
        if (user != null) {
            UserDetailsDto userDetails = UserDetailsDto.builder()
                    .id(user.getUuid())
                    .email(user.getEmail())
                    .login(user.getUsername())
                    .build();
            return ResponseEntity.ok(userDetails);
        } else {
            return ResponseEntity.status(401).build();
        }
    }

    public User getCurrentUser(HttpServletRequest request) {
        try {
            String token = null;
            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if ("Authorization".equals(cookie.getName())) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }

            if (token != null) {
                String username = jwtService.getSubject(token);
                return userRepository.findUserByLoginAndLockAndEnabled(username).orElse(null);
            }
        } catch (ExpiredJwtException | IllegalArgumentException e) {
            log.error("Token validation failed: {}", e.getMessage());
        }
        return null;
    }
}
