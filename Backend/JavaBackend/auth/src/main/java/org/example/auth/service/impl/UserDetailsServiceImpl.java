package org.example.auth.service.impl;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auth.dto.request.AddressRequest;
import org.example.auth.dto.request.AddressesChangeRequest;
import org.example.auth.dto.response.AddressResponse;
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
import org.example.auth.service.UserDetailsService;
import org.example.commonutils.Utils;
import org.example.exception.exceptions.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final ImageService imageService;
    private final Utils utils;
    private static final AddressMapper addressMapper = AddressMapper.INSTANCE;
    private final UserMapper userMapper = UserMapper.INSTANCE;

    @Override
    public ResponseEntity<UserDetailsResponse> fillUserPersonalData(UserPersonalDataRequest userPersonalDataRequest, HttpServletRequest request) {
        try {
            User currentUser = getCurrentUser(request);
            updatePersonalData(currentUser, userPersonalDataRequest);

            User user = userRepository.saveAndFlush(currentUser);
            UserDetailsResponse userDetailsResponse = UserMapper.INSTANCE.toUserDetailsResponse(user);

            return ResponseEntity.ok(userDetailsResponse);

        } catch (ResourceNotFoundException e) {
            log.error("Error in filling user personal data: User not found", e);
            throw e;
        } catch (UnauthorizedException e) {
            log.error("Unauthorized access while updating personal data", e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while updating user personal data", e);
            throw new UnExpectedError(
                    "An unexpected error occurred while updating user personal data",
                    e,
                    "UPDATE_USER_PERSONAL_DATA_ERROR",
                    Map.of("timestamp", LocalDateTime.now())
            );
        }
    }

    @Override
    @Transactional
    public ResponseEntity<List<AddressResponse>> updateUserAddresses(AddressesChangeRequest addressesChangeRequest, HttpServletRequest request) {
        try {
            User currentUser = getCurrentUser(request);
            addressesChangeRequest.getAddresses().forEach(addressRequest -> {
                try {
                    handleAddressOperation(addressRequest, currentUser);
                } catch (ResourceNotFoundException e) {
                    log.error("Address not found for operation: {}", addressRequest.getUuid(), e);
                    throw e;
                } catch (UnsupportedOperationException e) {
                    log.error("Unsupported operation: {}", addressRequest.getOperation(), e);
                    throw new InvalidParameterException(
                            "Unsupported address operation",
                            "UNSUPPORTED_OPERATION",
                            Map.of("operation", addressRequest.getOperation())
                    );
                } catch (Exception e) {
                    log.error("Unexpected error during address operation: {}", addressRequest, e);
                    throw new UnExpectedError(
                            "Failed to process address operation",
                            e,
                            "ADDRESS_OPERATION_ERROR",
                            Map.of("addressRequest", addressRequest)
                    );
                }
            });
            List<Address> updatedAddresses = addressRepository.findAllByUserId(currentUser.getId());
            List<AddressResponse> addressResponses = addressMapper.toAddressResponseList(updatedAddresses);
            return ResponseEntity.ok(addressResponses);

        } catch (UnsupportedOperationException e) {
            log.error("Unsupported operation: {}", e.getMessage(), e);
            throw e;
        } catch (ResourceNotFoundException e) {
            log.error("Error in updating user addresses: Resource not found", e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while updating user addresses", e);
            throw new UnExpectedError(
                    "An unexpected error occurred while updating user addresses",
                    e,
                    "UPDATE_USER_ADDRESSES_ERROR",
                    Map.of("timestamp", LocalDateTime.now())
            );
        }
    }

    @Override
    public ResponseEntity<ImageResponse> uploadImage(HttpServletRequest request, MultipartFile file) {
        try {
            String userUuid = utils.extractUserIdFromRequest(request);
            User user = userRepository.findUserByUuidAndIsActive(userUuid)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "User",
                            "uuid",
                            userUuid,
                            "USER_NOT_FOUND",
                            Map.of("uuid", userUuid)
                    ));

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

            userRepository.updateImageUrlById(user.getId(), newImageUrl);

            return ResponseEntity.ok(new ImageResponse(newImageUrl, "Image operation completed successfully"));

        } catch (ResourceNotFoundException e) {
            log.error("User not found while uploading image", e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while uploading image", e);
            throw new UnExpectedError(
                    "An unexpected error occurred while uploading the image",
                    e,
                    "UPLOAD_IMAGE_ERROR",
                    Map.of("timestamp", LocalDateTime.now())
            );
        }
    }

    @Override
    public ResponseEntity<?> deleteImage(HttpServletRequest request) {
        try {
            String userUuid = utils.extractUserIdFromRequest(request);

            User user = userRepository.findUserByUuidAndIsActive(userUuid)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "User",
                            "uuid",
                            userUuid,
                            "USER_NOT_FOUND",
                            Map.of("uuid", userUuid)
                    ));

            if (user.getImageUrl() == null || user.getImageUrl().isEmpty()) {
                log.info("No image to delete for user with UUID: {}", userUuid);
                return ResponseEntity.ok("No image to delete");
            }

            log.info("Deleting image for user with UUID: {}", userUuid);
            imageService.deleteImage(user.getImageUrl());

            userRepository.updateImageUrlById(user.getId(), null);
            log.info("Image deleted successfully for user with UUID: {}", userUuid);

            return ResponseEntity.ok("Image deleted successfully");

        } catch (ResourceNotFoundException e) {
            log.error("User not found while deleting image", e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while deleting image", e);
            throw new UnExpectedError(
                    "An unexpected error occurred while deleting the image",
                    e,
                    "DELETE_IMAGE_ERROR",
                    Map.of("timestamp", LocalDateTime.now())
            );
        }
    }

    @Override
    public ResponseEntity<UserDetailsResponse> getUserDetailsByUUid(String userUuid) {
        try {
            User user = userRepository.findUserByUuidAndIsActive(userUuid)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "User",
                            "uuid",
                            userUuid,
                            "USER_NOT_FOUND",
                            Map.of("uuid", userUuid)
                    ));
            if (!user.isActive()) {
                throw new UserIsUnActiveException("User is not active");
            }
            log.info("Successfully retrieved user details for UUID: {}", userUuid);
            return ResponseEntity.ok(userMapper.toUserDetailsResponse(user));

        } catch (ResourceNotFoundException e) {
            log.error("User not found for UUID: {}", userUuid, e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while retrieving user details for UUID: {}", userUuid, e);
            throw new UnExpectedError(
                    "An unexpected error occurred while retrieving user details",
                    e,
                    "GET_USER_DETAILS_ERROR",
                    Map.of("uuid", userUuid)
            );
        }
    }

    @Override
    public ResponseEntity<UserDetailsResponse> getUserDetails(HttpServletRequest request) {
        try {
            String userId = utils.extractUserIdFromRequest(request);
            User user = userRepository.findUserByUuidAndIsActive(userId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "User",
                            "uuid",
                            userId,
                            "USER_NOT_FOUND",
                            Map.of("uuid", userId)
                    ));

            return ResponseEntity.ok(userMapper.toUserDetailsResponse(user));

        } catch (UnauthorizedException e) {
            log.error("Unauthorized access while getting user details for request: {}", request.getRequestURI(), e);
            throw e;
        } catch (ResourceNotFoundException e) {
            log.error("User not found for UUID: {}", request.getRemoteUser(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while retrieving user details for request: {}", request.getRequestURI(), e);
            throw new UnExpectedError(
                    "An unexpected error occurred while retrieving user details",
                    e,
                    "GET_USER_DETAILS_ERROR",
                    Map.of("timestamp", LocalDateTime.now())
            );
        }
    }

    private void handleAddressOperation(AddressRequest addressRequest, User currentUser) {
        if (addressRequest.getOperation() == null) {
            throw new InvalidParameterException(
                    "Operation not specified in address request",
                    "OPERATION_NOT_SPECIFIED",
                    Map.of("addressRequest", addressRequest)
            );
        }

        switch (addressRequest.getOperation()) {
            case CREATE -> createAddress(addressRequest, currentUser);
            case UPDATE -> updateAddress(addressRequest);
            case DELETE -> deleteAddress(addressRequest);
            default -> throw new InvalidParameterException(
                    "Unsupported operation: " + addressRequest.getOperation(),
                    "UNSUPPORTED_OPERATION",
                    Map.of("operation", addressRequest.getOperation())
            );
        }
    }

    private void createAddress(AddressRequest addressRequest, User currentUser) {
        Address address = addressMapper.toAddress(addressRequest, currentUser);
        addressRepository.saveAndFlush(address);
    }

    private void updateAddress(AddressRequest addressRequest) {
        if (!addressRepository.existsByUuid(addressRequest.getUuid())) {
            throw new ResourceNotFoundException(
                    "Address",
                    "uuid",
                    addressRequest.getUuid(),
                    "ADDRESS_NOT_FOUND",
                    Map.of("uuid", addressRequest.getUuid())
            );
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
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Address",
                        "uuid",
                        addressRequest.getUuid(),
                        "ADDRESS_NOT_FOUND",
                        Map.of("uuid", addressRequest.getUuid())
                ));
        addressRepository.delete(addressToDelete);
    }

    private User getCurrentUser(HttpServletRequest request) {
        try {
            String userUuid = utils.extractUserIdFromRequest(request);
            User user = userRepository.findUserByUuidAndIsActive(userUuid)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "User",
                            "uuid",
                            userUuid,
                            "USER_NOT_FOUND",
                            Map.of("uuid", userUuid)
                    ));
            log.info("Successfully retrieved current user with UUID: {}", userUuid);
            return user;
        } catch (MalformedJwtException | IllegalArgumentException e) {
            log.error("Invalid token while retrieving current user", e);
            throw new UnauthorizedException(
                    "Invalid token",
                    "INVALID_TOKEN",
                    Map.of("error", e.getMessage())
            );
        } catch (ExpiredJwtException e) {
            log.error("Token expired while retrieving current user", e);
            throw new UnauthorizedException(
                    "Token has expired",
                    "TOKEN_EXPIRED",
                    Map.of("timestamp", LocalDateTime.now())
            );
        } catch (Exception e) {
            log.error("Unexpected error while retrieving current user", e);
            throw new UnExpectedError(
                    "An unexpected error occurred while retrieving current user",
                    e,
                    "GET_CURRENT_USER_ERROR",
                    Map.of("timestamp", LocalDateTime.now())
            );
        }
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
