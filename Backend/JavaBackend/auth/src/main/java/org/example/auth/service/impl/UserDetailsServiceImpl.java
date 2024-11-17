package org.example.auth.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auth.dto.request.AddressRequest;
import org.example.auth.dto.request.AddressesChangeRequest;
import org.example.auth.dto.response.ImageResponse;
import org.example.auth.dto.response.UserDetailsResponse;
import org.example.auth.dto.request.UserPersonalDataRequest;
import org.example.auth.entity.Address;
import org.example.auth.entity.User;
import org.example.auth.mapper.AddressMapper;
import org.example.auth.mapper.UserMapper;
import org.example.auth.repository.AddressRepository;
import org.example.auth.repository.UserRepository;
import org.example.auth.service.ImageService;
import org.example.auth.service.JwtService;
import org.example.auth.service.UserDetailsService;
import org.example.exception.exceptions.ApiRequestException;
import org.example.exception.exceptions.ResourceNotFoundException;
import org.example.exception.exceptions.UnauthorizedException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final JwtService jwtService;
    private final ImageService imageService;

    @Override
    public ResponseEntity<?> fillUserPersonalData(UserPersonalDataRequest userPersonalDataRequest, HttpServletRequest request) {
        try {
            User currentUser = getCurrentUser(request);

            currentUser.setFirstName(userPersonalDataRequest.getFirstName()==null ? currentUser.getFirstName() : userPersonalDataRequest.getFirstName());
            currentUser.setLastName(userPersonalDataRequest.getLastName()==null ? currentUser.getLastName() : userPersonalDataRequest.getLastName());
            currentUser.setPhoneNumber(userPersonalDataRequest.getPhoneNumber()==null ? currentUser.getPhoneNumber() : userPersonalDataRequest.getPhoneNumber());
            userRepository.save(currentUser);

            return ResponseEntity.ok("User personal data updated successfully");
        } catch (ResourceNotFoundException e) {
            log.error("Error in filling user personal data: User not found", e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while updating user personal data", e);
            throw new ApiRequestException("An error occurred while updating user personal data", e, "UPDATE_USER_PERSONAL_DATA_ERROR");
        }
    }

    @Override
    public ResponseEntity<?> updateUserAddresses(AddressesChangeRequest addressesChangeRequest, HttpServletRequest request) {
        try {
            User currentUser = getCurrentUser(request);

            for (AddressRequest addressRequest : addressesChangeRequest.getAddresses()) {
                switch (addressRequest.getOperation()) {
                    case CREATE -> createAddress(addressRequest, currentUser);
                    case UPDATE -> updateAddress(addressRequest);
                    case DELETE -> deleteAddress(addressRequest);
                    default -> throw new UnsupportedOperationException("Operation not supported: " + addressRequest.getOperation());
                }
            }
            return ResponseEntity.ok("Addresses updated successfully");
        } catch (ResourceNotFoundException e) {
            log.error("Error in updating user addresses: Resource not found", e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while updating user addresses", e);
            throw new ApiRequestException("An error occurred while updating user addresses", e, "UPDATE_USER_ADDRESSES_ERROR");
        }
    }

    @Override
    public ResponseEntity<?> uploadImage(HttpServletRequest request, MultipartFile file) throws Exception {
        String userId = jwtService.getUuidFromRequest(request);
        User user = userRepository.findByUuid(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "uuid", userId));
        if(user.getImageUrl()==null || user.getImageUrl().isEmpty()){
            String imageUrl = imageService.uploadImage(file);
            user.setImageUrl(imageUrl);
            userRepository.save(user);
            return ResponseEntity.ok(new ImageResponse(imageUrl, "Image uploaded successfully"));

        } else {
            imageService.deleteImage(user.getImageUrl());
            String imageUrl = imageService.uploadImage(file);
            user.setImageUrl(imageUrl);
            userRepository.save(user);
            return ResponseEntity.ok(new ImageResponse(imageUrl, "Image updated successfully"));
        }
    }

    @Override
    public ResponseEntity<?> deleteImage(HttpServletRequest request) throws Exception {
        String userId = jwtService.getUuidFromRequest(request);
        User user = userRepository.findByUuid(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "uuid", userId));
        if(user.getImageUrl().isEmpty()){
            return ResponseEntity.ok("No image to delete");
        } else {
            imageService.deleteImage(user.getImageUrl());
            user.setImageUrl("");
            userRepository.save(user);
            return ResponseEntity.ok("Image deleted successfully");
        }
    }

    private void createAddress(AddressRequest addressRequest, User currentUser) {
        Address address = AddressMapper.INSTANCE.toAddress(addressRequest, currentUser);
        addressRepository.save(address);
    }

    private void updateAddress(AddressRequest addressRequest) {
        addressRepository.updateAddress(
                addressRequest.getUuid(),
                addressRequest.getStreet(),
                addressRequest.getCity(),
                addressRequest.getState(),
                addressRequest.getPostalCode(),
                addressRequest.getCountry()
        );
    }

    private void deleteAddress(AddressRequest addressRequest) {
        Address addressToDelete = addressRepository.findByUuid(addressRequest.getUuid())
                .orElseThrow(() -> new ResourceNotFoundException("Address", "uuid", addressRequest.getUuid()));
        addressRepository.delete(addressToDelete);
    }

    private User getCurrentUser(HttpServletRequest request) {
        String userUuid = jwtService.getUuidFromRequest(request);
        return userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException("User", "uuid", userUuid));
    }

    @Override
    public ResponseEntity<?> getUserDetails(HttpServletRequest request) {
        try {
            String userId = jwtService.getUuidFromRequest(request);
            User user = userRepository.findByUuid(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "uuid", userId));
            return ResponseEntity.ok(new UserDetailsResponse(user));
        } catch (UnauthorizedException e) {
            log.error("Unauthorized access while getting user details", e);
            throw e;
        } catch (ResourceNotFoundException e) {
            log.error("User not found", e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while retrieving user details", e);
            throw new ApiRequestException("An error occurred while retrieving user details", e, "GET_USER_DETAILS_ERROR");
        }
    }
}
