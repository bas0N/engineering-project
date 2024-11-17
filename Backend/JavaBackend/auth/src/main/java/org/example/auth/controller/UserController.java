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

    @RequestMapping(path = "/details/personal-data", method = RequestMethod.PATCH)
    public ResponseEntity<?> userPersonalData(@Valid @RequestBody UserPersonalDataRequest userPersonalDataRequest, HttpServletRequest request) {
        return ResponseEntity.ok(userDetailsService.fillUserPersonalData(userPersonalDataRequest, request));
    }

    @RequestMapping(path = "/details/address", method = RequestMethod.PATCH)
    public ResponseEntity<?> updateAddresses(@Valid @RequestBody AddressesChangeRequest addressesChangeRequest, HttpServletRequest request) {
        return ResponseEntity.ok(userDetailsService.updateUserAddresses(addressesChangeRequest, request));
    }

    @RequestMapping(path = "/details", method = RequestMethod.GET)
    public ResponseEntity<?> userDetails(HttpServletRequest request) {
        return ResponseEntity.ok(userDetailsService.getUserDetails(request));
    }

    @RequestMapping(path = "/details/image", method = RequestMethod.POST)
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file, HttpServletRequest request) throws Exception {
        return userDetailsService.uploadImage(request, file);
    }

    @RequestMapping(path = "/details/image", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteImage(HttpServletRequest request) throws Exception {
        return userDetailsService.deleteImage(request);
    }



}
