package org.example.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.auth.dto.request.ChangePasswordRequest;
import org.example.auth.dto.request.LoginRequest;
import org.example.auth.dto.request.UserRegisterRequest;
import org.example.auth.dto.response.AuthResponse;

public interface UserService {

    void validateToken(HttpServletRequest request, HttpServletResponse response);

    void loggedIn(HttpServletRequest request, HttpServletResponse response);

    AuthResponse register(UserRegisterRequest userRegisterRequest);

    AuthResponse login(LoginRequest loginRequest);

    void authorize(HttpServletRequest request);

    AuthResponse changePassword(ChangePasswordRequest changePasswordRequest, HttpServletRequest request);
}
