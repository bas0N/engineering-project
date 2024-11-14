package org.example.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.auth.dto.request.AddressesChangeRequest;
import org.example.auth.dto.request.UserPersonalDataRequest;
import org.springframework.http.ResponseEntity;

public interface UserDetailsService {
    ResponseEntity<?> getUserDetails(HttpServletRequest request);

    ResponseEntity<?> fillUserPersonalData(UserPersonalDataRequest userPersonalDataRequest, HttpServletRequest request);

    ResponseEntity<?> updateUserAddresses(AddressesChangeRequest addressesChangeRequest, HttpServletRequest request);
}
