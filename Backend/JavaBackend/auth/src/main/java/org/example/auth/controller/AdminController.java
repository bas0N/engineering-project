package org.example.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auth.dto.response.UserAdminDetailsResponse;
import org.example.auth.entity.Role;
import org.example.auth.service.AdminService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {
    private final AdminService adminService;
    @DeleteMapping("/delete-account/{userUuid}")
    public ResponseEntity<?> deleteAccount(@PathVariable String userUuid) {
        return adminService.deleteAccount(userUuid);
    }

    @GetMapping("/user-details/{userId}")
    public ResponseEntity<UserAdminDetailsResponse> userDetails(@PathVariable Long userId) {
        return adminService.getUserById(userId);
    }

    @GetMapping("/all-users")
    public ResponseEntity<Page<UserAdminDetailsResponse>> allUsers(@RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size,
                                                                   @RequestParam(required = false) String email,
                                                                   @RequestParam(required = false) String firstName,
                                                                   @RequestParam(required = false) String lastName,
                                                                   @RequestParam(required = false) Boolean enabled,
                                                                   @RequestParam(required = false) Role role) {
        return adminService.getAllUsers(page, size, email, firstName, lastName, enabled, role);
    }

    @PatchMapping("/change-role/{userId}")
    public ResponseEntity<String> changeRole(@PathVariable Long userId, @RequestParam Role role) {
        return adminService.changeRole(userId, role);
    }

    @DeleteMapping("/delete-user/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        return adminService.deleteUser(userId);
    }

}
