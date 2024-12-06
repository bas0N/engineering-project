package org.example.auth.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auth.dto.response.UserAdminDetailsResponse;
import org.example.auth.entity.Role;
import org.example.auth.entity.User;
import org.example.auth.kafka.userDeActive.UserDeActiveProducer;
import org.example.auth.mapper.UserMapper;
import org.example.auth.repository.AddressRepository;
import org.example.auth.repository.UserRepository;
import org.example.auth.service.AdminService;
import org.example.exception.exceptions.ApiRequestException;
import org.example.exception.exceptions.InvalidParameterException;
import org.example.exception.exceptions.ResourceNotFoundException;
import org.example.exception.exceptions.UnExpectedError;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final UserMapper userMapper = UserMapper.INSTANCE;
    private final UserDeActiveProducer userDeActiveProducer;

    @Override
    public ResponseEntity<?> deleteAccount(String userUuid) {
        return null;
    }

    @Override
    public ResponseEntity<UserAdminDetailsResponse> getUserById(Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "User",
                            "id",
                            userId.toString(),
                            "USER_NOT_FOUND",
                            Map.of("userId", userId)
                    ));

            return ResponseEntity.ok(userMapper.toUserAdminDetailsResponse(user));

        } catch (ResourceNotFoundException e) {
            log.error("User not found for ID: {}", userId, e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while fetching user details for ID: {}", userId, e);
            throw new UnExpectedError(
                    "An unexpected error occurred while fetching user details",
                    e,
                    "GET_USER_BY_ID_ERROR",
                    Map.of("userId", userId)
            );
        }
    }

    @Override
    public ResponseEntity<Page<UserAdminDetailsResponse>> getAllUsers(int page, int size, String email, String firstName, String lastName, Boolean enabled, Role role) {
        try {
            if (page < 0 || size <= 0) {
                throw new InvalidParameterException(
                        "Page index must be >= 0 and size must be > 0",
                        "INVALID_PAGE_PARAMETERS",
                        Map.of("page", page, "size", size)
                );
            }

            Pageable pageable = PageRequest.of(page, size);

            Specification<User> spec = Specification.where(null);

            if (email != null && !email.isEmpty()) {
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
            }

            if (firstName != null && !firstName.isEmpty()) {
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%"));
            }

            if (lastName != null && !lastName.isEmpty()) {
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%"));
            }

            if (enabled != null) {
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("enabled"), enabled));
            }

            if (role != null) {
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("role"), role));
            }

            return ResponseEntity.ok(userRepository.findAll(spec, pageable).map(userMapper::toUserAdminDetailsResponse));

        } catch (IllegalArgumentException e) {
            log.error("Invalid parameters for fetching users: page={}, size={}, email={}, firstName={}, lastName={}, enabled={}, role={}",
                    page, size, email, firstName, lastName, enabled, role, e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while fetching users with filters: email={}, firstName={}, lastName={}, enabled={}, role={}",
                    email, firstName, lastName, enabled, role, e);
            throw new UnExpectedError(
                    "An unexpected error occurred while fetching users",
                    e,
                    "FETCH_USERS_ERROR",
                    Map.of(
                            "email", email,
                            "firstName", firstName,
                            "lastName", lastName,
                            "enabled", enabled,
                            "role", role
                    )
            );
        }
    }

    @Override
    public ResponseEntity<String> changeRole(Long userId, Role role) {
        try {
            if (role == null) {
                throw new InvalidParameterException(
                        "Role cannot be null",
                        "INVALID_ROLE",
                        Map.of("userId", userId)
                );
            }

            int updatedRows = userRepository.changeRole(userId, role);

            if (updatedRows == 0) {
                throw new ResourceNotFoundException(
                        "User",
                        "id",
                        userId.toString(),
                        "USER_NOT_FOUND",
                        Map.of("userId", userId)
                );
            }

            log.info("Role updated successfully for user with ID: {} to role: {}", userId, role);
            return ResponseEntity.ok("Role updated successfully");

        } catch (ResourceNotFoundException e) {
            log.error("User not found while changing role for ID: {}", userId, e);
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("Invalid role specified for user with ID: {}", userId, e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while changing role for user with ID: {}", userId, e);
            throw new UnExpectedError(
                    "An unexpected error occurred while changing user role",
                    e,
                    "CHANGE_USER_ROLE_ERROR",
                    Map.of("userId", userId, "role", role)
            );
        }
    }

    @Override
    public ResponseEntity<String> deleteUser(Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "User",
                            "id",
                            userId.toString(),
                            "USER_NOT_FOUND",
                            Map.of("userId", userId)
                    ));
            userRepository.deactivateAndClearUser(user.getId());
            addressRepository.deleteAddressesByUserId(user.getId());
            userDeActiveProducer.sendUserDeactivateEvent(user.getUuid());

            log.info("User deleted successfully with ID: {}", userId);
            return ResponseEntity.ok("User deleted successfully");

        } catch (ResourceNotFoundException e) {
            log.error("User not found while deleting user with ID: {}", userId, e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while deleting user with ID: {}", userId, e);
            throw new UnExpectedError(
                    "An unexpected error occurred while deleting user",
                    e,
                    "DELETE_USER_ERROR",
                    Map.of("userId", userId)
            );
        }
    }

}
