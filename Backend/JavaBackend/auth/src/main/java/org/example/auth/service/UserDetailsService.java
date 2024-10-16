package org.example.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.auth.dto.UserDetailsRequest;
import org.springframework.http.ResponseEntity;

public interface UserDetailsService {
    ResponseEntity<?> fillUserDetails(UserDetailsRequest userDetailsRequest, HttpServletRequest request);
    Object getUserDetails(HttpServletRequest request);
}
