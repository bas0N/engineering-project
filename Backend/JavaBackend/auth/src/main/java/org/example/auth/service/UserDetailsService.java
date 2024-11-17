package org.example.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.auth.dto.request.AddressesChangeRequest;
import org.example.auth.dto.request.UserPersonalDataRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface UserDetailsService {
    ResponseEntity<?> getUserDetails(HttpServletRequest request);

    ResponseEntity<?> fillUserPersonalData(UserPersonalDataRequest userPersonalDataRequest, HttpServletRequest request);

    ResponseEntity<?> updateUserAddresses(AddressesChangeRequest addressesChangeRequest, HttpServletRequest request);

    ResponseEntity<?> uploadImage(HttpServletRequest request, MultipartFile file) throws Exception;

    ResponseEntity<?> deleteImage(HttpServletRequest request) throws Exception;
}
