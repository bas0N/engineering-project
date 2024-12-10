package org.example.auth.controller;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auth.dto.request.ChangePasswordRequest;
import org.example.auth.dto.request.LoginRequest;
import org.example.auth.dto.request.UserRegisterRequest;
import org.example.auth.dto.response.AuthResponse;
import org.example.auth.entity.Code;
import org.example.auth.service.UserService;
import org.example.exception.exceptions.ApiRequestException;
import org.example.exception.exceptions.UnauthorizedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody UserRegisterRequest user) {
        return ResponseEntity.ok(userService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(userService.login(loginRequest));
    }

    @GetMapping("/logged-in")
    public ResponseEntity<?> loggedIn(HttpServletResponse response, HttpServletRequest request) {
        userService.loggedIn(request, response);
        return ResponseEntity.ok(new AuthResponse(Code.SUCCESS, null, null));
    }

    @GetMapping("/validate")
    public ResponseEntity<AuthResponse> validateToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            log.info("--START validateToken");
            userService.validateToken(request, response);
            log.info("--STOP validateToken");
            return ResponseEntity.ok(new AuthResponse(Code.PERMIT, null, null));
        } catch (ExpiredJwtException e) {
            log.info("Token has expired");
            throw new UnauthorizedException("Token has expired", "TOKEN_EXPIRED");
        } catch (IllegalArgumentException e) {
            log.info("Token is invalid");
            throw new ApiRequestException("Token is invalid", "INVALID_TOKEN");
        }
    }

    @GetMapping("/authorize")
    public ResponseEntity<AuthResponse> authorize(HttpServletRequest request, HttpServletResponse response) {
        try {
            log.info("--START authorize");
            userService.validateToken(request, response);
            userService.authorize(request);
            log.info("--STOP authorize");
            return ResponseEntity.ok(new AuthResponse(Code.PERMIT, null, null));
        } catch (ExpiredJwtException e) {
            log.info("Token is expired.");
            throw new UnauthorizedException("Token is expired.", "TOKEN_EXPIRED");
        } catch (IllegalArgumentException e) {
            log.info("Token is invalid.");
            throw new ApiRequestException("Invalid token.", "INVALID_TOKEN");
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<AuthResponse> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest, HttpServletRequest request) {
        return ResponseEntity.ok(userService.changePassword(changePasswordRequest, request));
    }

    @DeleteMapping("/delete-my-account")
    public ResponseEntity<?> deleteMyAccount(HttpServletRequest request) {
        return userService.deleteMyAccount(request);
    }

}
