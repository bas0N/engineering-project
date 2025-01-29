package org.example.auth.service;

import jakarta.transaction.Transactional;
import org.example.auth.dto.response.UserAdminDetailsResponse;
import org.example.auth.entity.Role;
import org.example.auth.entity.User;
import org.example.auth.repository.UserRepository;
import org.example.exception.exceptions.ApiRequestException;
import org.example.exception.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.kafka.bootstrap-servers=",
        "spring.kafka.listener.auto-startup=false"
})
@ActiveProfiles("test")
@Transactional
public class AdminServiceTest {
    @Autowired
    private AdminService adminService;
    @Autowired
    private UserRepository userRepository;
    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;
    @MockBean
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void getUserById_Success() {

        // Arrange
        //long userId = 1L;

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUuid("user-uuid-123");
        mockUser.setEmail("test@example.com");
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setRole(Role.ADMIN);
        mockUser.setEnabled(true);
        mockUser.setLock(false);

        UserAdminDetailsResponse mockResponse = new UserAdminDetailsResponse(
                1L,
                "user-uuid-123",
                "test@example.com",
                List.of(),
                null,
                null,
                "John",
                "Doe",
                null,
                Role.ADMIN,
                false,
                true,
                true
        );

        userRepository.save(mockUser);
        User savedUser = userRepository.findAll().getFirst();
        Long userId = savedUser.getId();

        // Act
        ResponseEntity<?> response = adminService.getUserById(userId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(UserAdminDetailsResponse.class, response.getBody());

        UserAdminDetailsResponse actualResponse = (UserAdminDetailsResponse) response.getBody();
        assertEquals(mockResponse.getUuid(), actualResponse.getUuid());
        assertEquals(mockResponse.getEmail(), actualResponse.getEmail());
        assertEquals(mockResponse.getFirstName(), actualResponse.getFirstName());
        assertEquals(mockResponse.getLastName(), actualResponse.getLastName());
        assertEquals(mockResponse.getRole(), actualResponse.getRole());
        assertEquals(mockResponse.isEnabled(), actualResponse.isEnabled());
        assertEquals(mockResponse.isLock(), actualResponse.isLock());
    }


    @Test
    void getAllUsers_Success() {
        // Arrange
        User user1 = new User(1L, "uuid1", "test1@example.com", null, Role.USER, false, true, true);
        User user2 = new User(2L, "uuid2", "test2@example.com", null, Role.USER, false, true, true);
        userRepository.save(user1);
        userRepository.save(user2);

        int page = 0;
        int size = 10;

        // Act
        ResponseEntity<?> response = adminService.getAllUsers(page, size, "test", null, null, true, Role.USER);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Page<UserAdminDetailsResponse> result = (Page<UserAdminDetailsResponse>) response.getBody();
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());

        List<UserAdminDetailsResponse> users = result.getContent();
        assertEquals("test1@example.com", users.get(0).getEmail());
        assertEquals("test2@example.com", users.get(1).getEmail());
    }


    @Test
    void changeRole_Success() {
        // Arrange
        Role newRole = Role.ADMIN;

        User user = new User();
        user.setRole(Role.USER);
        userRepository.save(user);
        userRepository.flush();
        Long userId = userRepository.findAll().getFirst().getId();

        // Act
        ResponseEntity<?> response = adminService.changeRole(userId, newRole);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void deleteUser_Success() {
        // Arrange
        User user = new User();
        userRepository.save(user);
        userRepository.flush();
        Long userId = userRepository.findAll().getFirst().getId();

        // Act
        ResponseEntity<?> response = adminService.deleteUser(userId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }


}
