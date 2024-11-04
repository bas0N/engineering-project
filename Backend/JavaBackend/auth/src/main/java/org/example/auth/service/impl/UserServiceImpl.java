package org.example.auth.service.impl;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auth.dto.*;
import org.example.auth.dto.request.ChangePasswordData;
import org.example.auth.dto.request.LoginRequest;
import org.example.auth.dto.response.AuthResponse;
import org.example.auth.entity.*;
import org.example.auth.mapper.UserMapper;
import org.example.auth.repository.ResetOperationsRepository;
import org.example.auth.repository.UserRepository;
import org.example.auth.repository.UserVersionRepository;
import org.example.auth.service.*;
import org.example.exception.exceptions.ApiRequestException;
import org.example.exception.exceptions.ResourceNotFoundException;
import org.example.exception.exceptions.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserVersionRepository userVersionRepository;
    private final PasswordEncoder passwordEncoder;
    private final ResetOperationService resetOperationService;
    private final ResetOperationsRepository resetOperationsRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final CookieService cookieService;
    @Value("${jwt.exp}")
    private int exp;
    @Value("${jwt.refresh.exp}")
    private int refreshExp;


    private User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.saveAndFlush(user);
    }

    private UserVersion saveUserVerison(UserVersion user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userVersionRepository.saveAndFlush(user);
    }

    private String generateToken(String email, String uuid, int exp) {
        return jwtService.generateToken(email, uuid, exp);
    }


    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        log.info("Delete all cookies");

        Cookie authCookie = cookieService.removeCookie(request.getCookies(), "Authorization");
        if (authCookie == null) {
            log.warn("Authorization cookie not found");
            throw new ApiRequestException("Authorization cookie not found", "COOKIE_NOT_FOUND");
        }
        response.addCookie(authCookie);

        Cookie refreshCookie = cookieService.removeCookie(request.getCookies(), "refresh");
        if (refreshCookie == null) {
            log.warn("Refresh token cookie not found");
            throw new ApiRequestException("Refresh token cookie not found", "COOKIE_NOT_FOUND");
        }
        response.addCookie(refreshCookie);

        return ResponseEntity.ok(new AuthResponse(Code.SUCCESS));
    }

    public void validateToken(HttpServletRequest request, HttpServletResponse response) throws ExpiredJwtException, IllegalArgumentException {
        String token = null;
        String refresh = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("Authorization".equals(cookie.getName())) {
                    token = cookie.getValue();
                } else if ("refresh".equals(cookie.getName())) {
                    refresh = cookie.getValue();
                }
            }
        } else {
            log.info("No cookies found in the request");
            throw new IllegalArgumentException("Cookies cannot be null");
        }

        try {
            if (token != null) {
                jwtService.validateToken(token);
            } else {
                throw new IllegalArgumentException("Authorization token is missing");
            }
        } catch (ExpiredJwtException | IllegalArgumentException e) {
            log.info("Authorization token is invalid or expired, attempting to validate refresh token");
            if (refresh != null) {
                jwtService.validateToken(refresh);
                String newAuthToken = jwtService.refreshToken(refresh, exp);
                String newRefreshToken = jwtService.refreshToken(refresh, refreshExp);

                Cookie newAuthCookie = cookieService.generateCookie("Authorization", newAuthToken, exp);
                Cookie newRefreshCookie = cookieService.generateCookie("refresh", newRefreshToken, refreshExp);

                response.addCookie(newAuthCookie);
                response.addCookie(newRefreshCookie);
            } else {
                log.info("Refresh token is missing");
                throw new UnauthorizedException("Refresh token is missing or invalid", "REFRESH_TOKEN_MISSING");
            }
        }
    }

    public void loggedIn(HttpServletRequest request, HttpServletResponse response) {
        try {
            validateToken(request, response);  // Walidacja tokena
        } catch (ExpiredJwtException e) {
            log.info("Token is expired.");
            throw new UnauthorizedException("Token is expired.", "TOKEN_EXPIRED");

        } catch (IllegalArgumentException e) {
            log.info("Token is invalid.");
            throw new ApiRequestException("Invalid token.", e, "INVALID_TOKEN");
        }
    }

    public void loginByToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            validateToken(request, response);

            String refresh = null;
            for (Cookie value : Arrays.stream(request.getCookies()).toList()) {
                if (value.getName().equals("refresh")) {
                    refresh = value.getValue();
                }
            }

            if (refresh == null) {
                throw new UnauthorizedException("Refresh token not found in cookies.", "REFRESH_TOKEN_MISSING");
            }

//            String email = jwtService.getSubject(refresh);
//
//            User user = userRepository.findUserByEmail(email)
//                    .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        } catch (ExpiredJwtException e) {
            log.info("Can't login, token is expired.");
            throw new UnauthorizedException("Token is expired.", "TOKEN_EXPIRED", Map.of("token", "refresh"));
        } catch (IllegalArgumentException e) {
            log.info("Can't login, token is invalid.");
            throw new ApiRequestException("Invalid token.", e, "INVALID_TOKEN");
        }
    }

    public void register(UserRegisterRequest userRegisterRequest) {
        userRepository.findUserByEmail(userRegisterRequest.getEmail()).ifPresent(value -> {
            log.info("Users alredy exist with this mail");
            throw new ApiRequestException("User already exists with this email", "EMAIL_EXISTS");
        });
        User user = UserMapper.INSTANCE.mapUserRegisterDtoToUser(userRegisterRequest);
        UserVersion userVersion = new UserVersion(user);
        saveUser(user);
        saveUserVerison(userVersion);

    }

    public void login(HttpServletResponse response, LoginRequest loginRequest) {
        User user = userRepository.findUserByEmailAndLockAndEnabled(loginRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", loginRequest.getEmail()));
        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            if (authenticate.isAuthenticated()) {
                String authToken = jwtService.generateToken(user.getEmail(), user.getUuid(), exp);
                String refreshToken = jwtService.generateToken(user.getEmail(), user.getUuid(), refreshExp);

                Cookie authCookie = cookieService.generateCookie("Authorization", authToken, exp);
                Cookie refreshCookie = cookieService.generateCookie("refresh", refreshToken, refreshExp);

                response.addCookie(authCookie);
                response.addCookie(refreshCookie);
            } else {
                log.info("--STOP LoginService: Invalid credentials");
                throw new UnauthorizedException("Invalid credentials", "INVALID_CREDENTIALS");
            }
        } catch (Exception e) {
            log.info("Authentication failed", e);
            throw new ApiRequestException("Authentication failed", e, "AUTH_FAILED");
        }
    }


    public void setAsAdmin(UserRegisterRequest user) {
//        userRepository.findUserByLogin(user.getLogin()).ifPresent(value -> {
//            value.setRole(Role.ADMIN);
//            userRepository.save(value);
//        });
    }

    public void activateUser(String uid) {
        User user = userRepository.findUserByUuid(uid).orElse(null);
        if (user != null) {
            user.setLock(false);
            user.setEnabled(true);
            userRepository.save(user);
            return;
        }
    }

    public void recoveryPassword(String email) {
        User user = userRepository.findUserByEmail(email).orElse(null);
        if (user != null) {
            ResetOperations resetOperations = resetOperationService.initResetOperation(user);
            emailService.sendPasswordRecovery(user, resetOperations.getUid());
            return;
        }
    }

    public void restPassword(ChangePasswordData changePasswordData) {
        ResetOperations resetOperations = resetOperationsRepository.findByUid(changePasswordData.getUid()).orElse(null);
        if (resetOperations != null) {
            User user = userRepository.findUserByUuid(resetOperations.getUser().getUuid()).orElse(null);

            if (user != null) {
                user.setPassword(changePasswordData.getPassword());
                saveUser(user);
                resetOperationService.endOperation(resetOperations.getUid());
                return;
            }
        }
    }

    public void authorize(HttpServletRequest request) {
        String token = null;
        String refresh = null;

        // Pobieranie tokenów z ciasteczek
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("Authorization".equals(cookie.getName())) {
                    token = cookie.getValue();
                } else if ("refresh".equals(cookie.getName())) {
                    refresh = cookie.getValue();
                }
            }
        } else {
            log.info("No cookies found in the request.");
            throw new IllegalArgumentException("Cookies cannot be null.");
        }

        String subject;

        if (token != null && !token.isEmpty()) {
            subject = jwtService.getSubject(token);  // Pobieranie subject (login lub uuid) z tokena
        } else if (refresh != null && !refresh.isEmpty()) {
            subject = jwtService.getSubject(refresh);  // Pobieranie subject z tokena odświeżającego
        } else {
            throw new IllegalArgumentException("Both Authorization and refresh tokens are missing.");
        }
    }
}
