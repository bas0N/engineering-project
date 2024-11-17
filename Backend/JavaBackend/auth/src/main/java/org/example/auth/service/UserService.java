package org.example.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.auth.dto.request.ChangePasswordData;
import org.example.auth.dto.request.LoginRequest;
import org.example.auth.dto.request.UserRegisterRequest;
import org.example.auth.dto.response.AuthResponse;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response);

    void validateToken(HttpServletRequest request, HttpServletResponse response);

    void loggedIn(HttpServletRequest request, HttpServletResponse response);

    void loginByToken(HttpServletRequest request, HttpServletResponse response);

    AuthResponse register(UserRegisterRequest userRegisterRequest);

    AuthResponse login(LoginRequest loginRequest);

    void setAsAdmin(UserRegisterRequest user);

    void activateUser(String uid);

    void recoveryPassword(String email);

    void restPassword(ChangePasswordData changePasswordData);

    void authorize(HttpServletRequest request);


}
