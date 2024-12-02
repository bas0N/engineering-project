package org.example.auth.service.impl;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auth.dto.request.ChangePasswordRequest;
import org.example.auth.dto.request.LoginRequest;
import org.example.auth.dto.request.UserRegisterRequest;
import org.example.auth.dto.response.AuthResponse;
import org.example.auth.entity.*;
import org.example.auth.mapper.UserMapper;
import org.example.auth.repository.UserRepository;
import org.example.auth.service.*;
import org.example.exception.exceptions.ApiRequestException;
import org.example.exception.exceptions.ResourceNotFoundException;
import org.example.exception.exceptions.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    @Value("${jwt.exp}")
    private int exp;
    @Value("${jwt.refresh.exp}")
    private int refreshExp;


    private void saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.saveAndFlush(user);
    }

    @Override
    public void validateToken(HttpServletRequest request, HttpServletResponse response) throws ExpiredJwtException, IllegalArgumentException {
        String accessToken = extractToken(request.getHeader("Authorization"));
        String refreshToken = extractToken(request.getHeader("Refresh-Token"));

        if (accessToken == null) {
            throw new UnauthorizedException("Access token is missing or invalid", "ACCESS_TOKEN_MISSING");
        }

        try {
            jwtService.validateToken(accessToken);
        } catch (ExpiredJwtException e) {
            log.info("Access token expired. Attempting to validate refresh token.");
            if (refreshToken != null) {
                jwtService.validateToken(refreshToken);

                String newAuthToken = jwtService.refreshToken(refreshToken, exp);
                String newRefreshToken = jwtService.refreshToken(refreshToken, refreshExp);

                response.setHeader("Authorization", "Bearer " + newAuthToken);
                response.setHeader("Refresh-Token", "Bearer " + newRefreshToken);

                log.info("New tokens issued successfully.");
            } else {
                throw new UnauthorizedException("Refresh token is missing or invalid.", "REFRESH_TOKEN_MISSING");
            }
        } catch (MalformedJwtException | IllegalArgumentException e) {
            log.info("Invalid access token.");
            throw new ApiRequestException("Invalid token.", e, "INVALID_TOKEN");
        }
    }

    @Override
    public void loggedIn(HttpServletRequest request, HttpServletResponse response) {
        try {
            validateToken(request, response);
        } catch (ExpiredJwtException e) {
            log.info("Token is expired.");
            throw new UnauthorizedException("Token is expired.", "TOKEN_EXPIRED");

        } catch (IllegalArgumentException e) {
            log.info("Token is invalid.");
            throw new ApiRequestException("Invalid token.", e, "INVALID_TOKEN");
        }
    }

    @Override
    public AuthResponse register(UserRegisterRequest userRegisterRequest) {
        try {
            userRepository.findUserByEmail(userRegisterRequest.getEmail()).ifPresent(value -> {
                log.info("User already exists with this email");
                throw new ApiRequestException("User already exists with this email", "EMAIL_EXISTS");
            });
            User user = UserMapper.INSTANCE.mapUserRegisterDtoToUser(userRegisterRequest);

            saveUser(user);

            String authToken = jwtService.generateToken(user.getEmail(), user.getUuid(), exp);
            String refreshToken = jwtService.generateToken(user.getEmail(), user.getUuid(), refreshExp);

            return new AuthResponse(Code.SUCCESS, authToken, refreshToken);
        } catch (ApiRequestException e) {
            log.error("ApiRequestException during registration: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during user registration: {}", e.getMessage(), e);
            throw new ApiRequestException("Unexpected error during registration", e, "INTERNAL_ERROR");
        }
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        User user = userRepository.findUserByEmailAndLockAndEnabled(loginRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", loginRequest.getEmail()));

        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
            if (authenticate.isAuthenticated()) {
                String authToken = jwtService.generateToken(user.getEmail(), user.getUuid(), exp);
                String refreshToken = jwtService.generateToken(user.getEmail(), user.getUuid(), refreshExp);

                return new AuthResponse(Code.SUCCESS, authToken, refreshToken);
            } else {
                log.info("--STOP LoginService: Invalid credentials");
                throw new UnauthorizedException("Invalid credentials", "INVALID_CREDENTIALS");
            }
        } catch (BadCredentialsException e) {
            log.error("Invalid credentials: {}", e.getMessage());
            throw new UnauthorizedException("Invalid credentials", "INVALID_CREDENTIALS");
        } catch (Exception e) {
            log.error("Authentication failed: {}", e.getMessage());
            throw new ApiRequestException("Authentication failed", e, "AUTH_FAILED");
        }
    }

    @Override
    public void authorize(HttpServletRequest request) {
        try {
            String accessToken = extractToken(request.getHeader("Authorization"));

            if (accessToken == null || accessToken.isEmpty()) {
                throw new UnauthorizedException("Access token is missing or invalid.", "ACCESS_TOKEN_MISSING");
            }

            jwtService.validateToken(accessToken);

            String email = jwtService.getEmailFromToken(accessToken);
            User user = userRepository.findUserByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

            if (user.getRole() != Role.ADMIN) {
                log.warn("Access denied: User is not an admin. Role: {}", user.getRole());
                throw new UnauthorizedException("Access denied. Admin role required.", "ACCESS_DENIED");
            }

            log.info("User authorized successfully as ADMIN.");
        } catch (UnauthorizedException e) {
            log.error("Authorization failed: {}", e.getMessage());
            throw e;
        } catch (ResourceNotFoundException e) {
            log.error("User not found: {}", e.getMessage());
            throw new UnauthorizedException("User not found.", "USER_NOT_FOUND");
        } catch (MalformedJwtException e) {
            log.error("Invalid token format: {}", e.getMessage());
            throw new UnauthorizedException("Invalid token format.", "INVALID_TOKEN_FORMAT");
        } catch (Exception e) {
            log.error("Unexpected error during authorization: {}", e.getMessage(), e);
            throw new ApiRequestException("Unexpected error during authorization.", e, "AUTHORIZATION_ERROR");
        }
    }

    @Override
    public AuthResponse changePassword(ChangePasswordRequest changePasswordRequest, HttpServletRequest request) {
        try {
            String token = extractToken(request.getHeader("Authorization"));
            String userUuid = jwtService.getUuidFromToken(token);
            User user = userRepository.findUserByUuid(userUuid)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "uuid", userUuid));
            if (passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
                user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
                userRepository.save(user);
                return new AuthResponse(Code.SUCCESS, null, null);
            } else {
                throw new UnauthorizedException(
                        "Invalid old password",
                        "INVALID_PASSWORD",
                        Map.of("userUuid", userUuid, "reason", "Old password does not match")
                );
            }
        } catch (MalformedJwtException | IllegalArgumentException e) {
            throw new UnauthorizedException(
                    "Invalid token",
                    "INVALID_TOKEN",
                    Map.of("reason", "Malformed or missing token", "timestamp", LocalDateTime.now())
            );
        }
    }

    private String extractToken(String header) {
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
