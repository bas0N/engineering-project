package org.example.auth.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
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
    private static final AddressMapper addressMapper = AddressMapper.INSTANCE;

    @Override
    public ResponseEntity<?> fillUserPersonalData(UserPersonalDataRequest userPersonalDataRequest, HttpServletRequest request) {
        try {
            User currentUser = getCurrentUser(request);
            updatePersonalData(currentUser, userPersonalDataRequest);
            userRepository.save(currentUser);

            return ResponseEntity.ok("User personal data updated successfully");
        } catch (ResourceNotFoundException e) {
            log.error("Error in filling user personal data: User not found", e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while updating user personal data", e);
            throw new ApiRequestException(
                    "An error occurred while updating user personal data",
                    e,
                    "UPDATE_USER_PERSONAL_DATA_ERROR"
            );
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> updateUserAddresses(AddressesChangeRequest addressesChangeRequest, HttpServletRequest request) {
        try {
            User currentUser = getCurrentUser(request);
            //tutaj jeszcze rolbakc jezlei sie nie uda
            addressesChangeRequest.getAddresses().forEach(addressRequest -> {
                try {
                    handleAddressOperation(addressRequest, currentUser);
                } catch (ResourceNotFoundException e) {
                    log.error("Address not found for operation: {}", addressRequest.getUuid(), e);
                    throw e;
                } catch (UnsupportedOperationException e) {
                    log.error("Unsupported operation: {}", addressRequest.getOperation(), e);
                    throw e;
                } catch (Exception e) {
                    log.error("Unexpected error during address operation: {}", addressRequest, e);
                    throw new ApiRequestException("Failed to process address operation", e, "ADDRESS_OPERATION_ERROR");
                }
            });
            return ResponseEntity.ok("Addresses updated successfully");
        } catch (UnsupportedOperationException e) {
            log.error("Unsupported operation: {}", e.getMessage(), e);
            throw e;
        } catch (ResourceNotFoundException e) {
            log.error("Error in updating user addresses: Resource not found", e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while updating user addresses", e);
            throw new ApiRequestException("An error occurred while updating user addresses", e, "UPDATE_USER_ADDRESSES_ERROR");
        }
    }

    @Override
    public ResponseEntity<?> uploadImage(HttpServletRequest request, MultipartFile file) {
        try {
            String userUuid = jwtService.getUuidFromRequest(request);
            User user = userRepository.findByUuid(userUuid)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "uuid", userUuid));

            String newImageUrl;

            if (user.getImageUrl() == null || user.getImageUrl().isEmpty()) {
                log.info("Uploading new image for user with UUID: {}", userUuid);
                newImageUrl = imageService.uploadImage(file);
                log.info("Image uploaded successfully for user with UUID: {}", userUuid);
            } else {
                log.info("Updating image for user with UUID: {}", userUuid);
                imageService.deleteImage(user.getImageUrl());
                newImageUrl = imageService.uploadImage(file);
                log.info("Image updated successfully for user with UUID: {}", userUuid);
            }

            userRepository.updateImageUrlByUuid(userUuid, newImageUrl);

            return ResponseEntity.ok(new ImageResponse(newImageUrl, "Image operation completed successfully"));
        } catch (ResourceNotFoundException e) {
            log.error("User not found while uploading image", e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while uploading image", e);
            throw new ApiRequestException("An error occurred while uploading the image", e, "UPLOAD_IMAGE_ERROR");
        }
    }

    @Override
    public ResponseEntity<?> deleteImage(HttpServletRequest request) {
        try {
            String userUuid = jwtService.getUuidFromRequest(request);

            User user = userRepository.findByUuid(userUuid)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "uuid", userUuid));

            if (user.getImageUrl() == null || user.getImageUrl().isEmpty()) {
                log.info("No image to delete for user with UUID: {}", userUuid);
                return ResponseEntity.ok("No image to delete");
            }

            log.info("Deleting image for user with UUID: {}", userUuid);
            imageService.deleteImage(user.getImageUrl());

            userRepository.updateImageUrlByUuid(userUuid, null);
            log.info("Image deleted successfully for user with UUID: {}", userUuid);

            return ResponseEntity.ok("Image deleted successfully");

        } catch (ResourceNotFoundException e) {
            log.error("User not found while deleting image", e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while deleting image", e);
            throw new ApiRequestException("An error occurred while deleting the image", e, "DELETE_IMAGE_ERROR");
        }
    }

    @Override
    public ResponseEntity<?> deleteMyAccount(HttpServletRequest request) {
        ///trzeba to zaklepac
        return null;
    }

    @Override
    public ResponseEntity<?> getUserDetailsByUUid(String userUuid) {
        try {
            User user = userRepository.findByUuid(userUuid)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "uuid", userUuid));
            log.info("Successfully retrieved user details for UUID: {}", userUuid);
            return ResponseEntity.ok(new UserDetailsResponse(user));

        } catch (ResourceNotFoundException e) {
            log.error("User not found for UUID: {}", userUuid, e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while retrieving user details for UUID: {}", userUuid, e);
            throw new ApiRequestException("An error occurred while retrieving user details", e, "GET_USER_DETAILS_ERROR");
        }
    }

    @Override
    public ResponseEntity<?> getUserDetails(HttpServletRequest request) {
        try {
            String userId = jwtService.getUuidFromRequest(request);
            User user = userRepository.findByUuid(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "uuid", userId));

            return ResponseEntity.ok(new UserDetailsResponse(user));
        } catch (UnauthorizedException e) {
            log.error("Unauthorized access while getting user details for request: {}", request.getRequestURI(), e);
            throw e;
        } catch (ResourceNotFoundException e) {
            log.error("User not found for UUID: {}", request.getRemoteUser(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while retrieving user details for request: {}", request.getRequestURI(), e);
            throw new ApiRequestException("An error occurred while retrieving user details", e, "GET_USER_DETAILS_ERROR");
        }
    }

    private void handleAddressOperation(AddressRequest addressRequest, User currentUser) {
        if (addressRequest.getOperation() == null) {
            throw new UnsupportedOperationException("Operation not supported: null");
        }

        switch (addressRequest.getOperation()) {
            case CREATE -> createAddress(addressRequest, currentUser);
            case UPDATE -> updateAddress(addressRequest);
            case DELETE -> deleteAddress(addressRequest);
            default ->
                    throw new UnsupportedOperationException("Operation not supported: " + addressRequest.getOperation());
        }
    }

    private void createAddress(AddressRequest addressRequest, User currentUser) {
        Address address = addressMapper.toAddress(addressRequest, currentUser);
        addressRepository.save(address);
    }

    private void updateAddress(AddressRequest addressRequest) {
        if (!addressRepository.existsByUuid(addressRequest.getUuid())) {
            throw new ResourceNotFoundException("Address", "uuid", addressRequest.getUuid());
        }
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

    private void updatePersonalData(User currentUser, UserPersonalDataRequest userPersonalDataRequest) {
        if (userPersonalDataRequest.getFirstName() != null) {
            currentUser.setFirstName(userPersonalDataRequest.getFirstName());
        }

        if (userPersonalDataRequest.getLastName() != null) {
            currentUser.setLastName(userPersonalDataRequest.getLastName());
        }

        if (userPersonalDataRequest.getPhoneNumber() != null) {
            currentUser.setPhoneNumber(userPersonalDataRequest.getPhoneNumber());
        }
    }

}
