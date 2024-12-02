package org.example.auth.service;

import org.example.auth.entity.Role;
import org.springframework.http.ResponseEntity;

public interface AdminService {
    ResponseEntity<?> deleteAccount(String userUuid);

    ResponseEntity<?> getUserById(Long userId);

    ResponseEntity<?> getAllUsers(int page, int size, String email, String firstName, String lastName, Boolean enabled, Role role);

    ResponseEntity<?> changeRole(Long userId, Role role);
}
