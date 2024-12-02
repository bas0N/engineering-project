package org.example.auth.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auth.entity.Role;
import org.example.auth.entity.User;
import org.example.auth.mapper.UserMapper;
import org.example.auth.repository.UserRepository;
import org.example.auth.service.AdminService;
import org.example.exception.exceptions.ApiRequestException;
import org.example.exception.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final UserMapper userMapper = UserMapper.INSTANCE;

    @Override
    public ResponseEntity<?> deleteAccount(String userUuid) {
        return null;
    }

    @Override
    public ResponseEntity<?> getUserById(Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId.toString()));

            return ResponseEntity.ok(userMapper.toUserAdminDetailsResponse(user));

        } catch (ResourceNotFoundException e) {
            log.error("User not found for ID: {}", userId, e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while fetching user details for ID: {}", userId, e);
            throw new ApiRequestException("An error occurred while fetching user details", e, "GET_USER_BY_ID_ERROR");
        }
    }

    @Override
    public ResponseEntity<?> getAllUsers(int page, int size, String email, String firstName, String lastName, Boolean enabled, Role role) {
        try {
            if (page < 0 || size <= 0) {
                throw new IllegalArgumentException("Page index must be >= 0 and size must be > 0");
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
            throw new ApiRequestException("Invalid parameters for fetching users", e, "INVALID_QUERY_PARAMETERS");
        } catch (Exception e) {
            log.error("Unexpected error while fetching users with filters: email={}, firstName={}, lastName={}, enabled={}, role={}",
                    email, firstName, lastName, enabled, role, e);
            throw new ApiRequestException("An error occurred while fetching users", e, "FETCH_USERS_ERROR");
        }
    }

    @Override
    public ResponseEntity<?> changeRole(Long userId, Role role) {
        try {
            int updatedRows = userRepository.changeRole(userId, role);

            if (updatedRows == 0) {
                throw new ResourceNotFoundException("User", "id", userId.toString());
            }

            log.info("Role updated successfully for user with ID: {} to role: {}", userId, role);
            return ResponseEntity.ok().build();

        } catch (ResourceNotFoundException e) {
            log.error("User not found while changing role for ID: {}", userId, e);
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("Invalid role specified for user with ID: {}", userId, e);
            throw new ApiRequestException("Invalid role specified", e, "INVALID_ROLE_ERROR");
        } catch (Exception e) {
            log.error("Unexpected error while changing role for user with ID: {}", userId, e);
            throw new ApiRequestException("An error occurred while changing user role", e, "CHANGE_USER_ROLE_ERROR");
        }
    }

}
