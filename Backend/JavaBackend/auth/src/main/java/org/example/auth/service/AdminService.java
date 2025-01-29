package org.example.auth.service;

import org.example.auth.dto.response.UserAdminDetailsResponse;
import org.example.auth.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

public interface AdminService {

    ResponseEntity<UserAdminDetailsResponse> getUserById(Long userId);

    ResponseEntity<Page<UserAdminDetailsResponse>> getAllUsers(int page, int size, String email, String firstName, String lastName, Boolean enabled, Role role);

    ResponseEntity<String> changeRole(Long userId, Role role);

    ResponseEntity<String> deleteUser(Long userId);
}
