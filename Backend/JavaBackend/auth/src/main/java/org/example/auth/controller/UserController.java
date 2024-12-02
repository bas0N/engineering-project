package org.example.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auth.dto.request.AddressesChangeRequest;
import org.example.auth.dto.request.UserPersonalDataRequest;
import org.example.auth.service.UserDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/auth/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserDetailsService userDetailsService;

    @PatchMapping("/details/personal-data")
    public ResponseEntity<?> userPersonalData(@Valid @RequestBody UserPersonalDataRequest userPersonalDataRequest, HttpServletRequest request) {
        return userDetailsService.fillUserPersonalData(userPersonalDataRequest, request);
    }

    @PatchMapping("/details/address")
    public ResponseEntity<?> updateAddresses(@Valid @RequestBody AddressesChangeRequest addressesChangeRequest, HttpServletRequest request) {
        return userDetailsService.updateUserAddresses(addressesChangeRequest, request);
    }

    @GetMapping("/details")
    public ResponseEntity<?> userDetails(HttpServletRequest request) {
        return userDetailsService.getUserDetails(request);
    }

    @GetMapping("/details/{userId}")
    public ResponseEntity<?> userDetails(@PathVariable String userId) {
        return userDetailsService.getUserDetailsByUUid(userId);
    }

    @PostMapping("/details/image")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file, HttpServletRequest request) throws Exception {
        return userDetailsService.uploadImage(request, file);
    }

    @DeleteMapping("/details/image")
    public ResponseEntity<?> deleteImage(HttpServletRequest request) throws Exception {
        return userDetailsService.deleteImage(request);
    }

    @DeleteMapping("/delete-my-account")
    public ResponseEntity<?> deleteMyAccount(HttpServletRequest request) {
        return userDetailsService.deleteMyAccount(request);
    }

}
