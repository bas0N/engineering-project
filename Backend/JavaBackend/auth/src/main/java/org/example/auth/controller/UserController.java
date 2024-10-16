package org.example.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auth.dto.UserDetailsRequest;
import org.example.auth.service.UserDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserDetailsService userDetailsService;

    @RequestMapping(path = "/details", method = RequestMethod.PATCH)
    public ResponseEntity<?> userDetails(@Valid @RequestBody UserDetailsRequest userDetailsRequest, HttpServletRequest request) {
        return ResponseEntity.ok(userDetailsService.fillUserDetails(userDetailsRequest, request));
    }

    @RequestMapping(path = "/details", method = RequestMethod.GET)
    public ResponseEntity<?> userDetails(HttpServletRequest request) {
        return ResponseEntity.ok(userDetailsService.getUserDetails(request));
    }
}
