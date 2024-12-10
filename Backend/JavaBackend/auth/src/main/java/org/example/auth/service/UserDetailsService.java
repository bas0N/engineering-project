package org.example.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.auth.dto.request.AddressesChangeRequest;
import org.example.auth.dto.request.UserPersonalDataRequest;
import org.example.auth.dto.response.AddressResponse;
import org.example.auth.dto.response.ImageResponse;
import org.example.auth.dto.response.UserDetailsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserDetailsService {
    ResponseEntity<UserDetailsResponse> getUserDetails(HttpServletRequest request);

    ResponseEntity<UserDetailsResponse> fillUserPersonalData(UserPersonalDataRequest userPersonalDataRequest, HttpServletRequest request);

    ResponseEntity<List<AddressResponse>> updateUserAddresses(AddressesChangeRequest addressesChangeRequest, HttpServletRequest request);

    ResponseEntity<ImageResponse> uploadImage(HttpServletRequest request, MultipartFile file) throws Exception;

    ResponseEntity<?> deleteImage(HttpServletRequest request) throws Exception;

    ResponseEntity<UserDetailsResponse> getUserDetailsByUUid(String userId);
}
