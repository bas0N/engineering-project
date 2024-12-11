package org.example.auth.service;

import jakarta.transaction.Transactional;
import org.example.auth.dto.request.ChangePasswordRequest;
import org.example.auth.dto.request.LoginRequest;
import org.example.auth.dto.request.UserRegisterRequest;
import org.example.auth.dto.response.AuthResponse;
import org.example.auth.entity.Code;
import org.example.auth.entity.Role;
import org.example.auth.entity.User;
import org.example.auth.repository.UserRepository;
import org.example.exception.exceptions.ApiRequestException;
import org.example.exception.exceptions.ResourceNotFoundException;
import org.example.exception.exceptions.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.kafka.bootstrap-servers=",
        "spring.kafka.listener.auto-startup=false"
})
@ActiveProfiles("test")
@Transactional
public class UserServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    private static final int exp = 3600;
    private static final int refreshExp = 86400;


    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;

    @MockBean
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    ///REGISTER
    @Test
    void Register_Success() {
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");

        AuthResponse response = userService.register(registerRequest);

        User savedUser = userRepository.findUserByEmail("test@example.com").orElse(null);

        assertNotNull(savedUser);
        assertEquals("test@example.com", savedUser.getEmail());
        assertTrue(passwordEncoder.matches("password123", savedUser.getPassword()));
        assertNotNull(response.getToken());
        assertNotNull(response.getRefreshToken());
    }

    @Test
    void Register_Fail_EmailAlreadyExists() {
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        userService.register(registerRequest);

        UserRegisterRequest duplicateRegisterRequest = new UserRegisterRequest();
        duplicateRegisterRequest.setEmail("test@example.com");
        duplicateRegisterRequest.setPassword("newpassword123");

        ApiRequestException exception = assertThrows(ApiRequestException.class, () -> {
            userService.register(duplicateRegisterRequest);
        });

        assertEquals("User already exists with this email", exception.getMessage());
        assertEquals("EMAIL_EXISTS", exception.getErrorCode());
    }

    //LOGIN

    @Test
    void Login_Success() {
        String email = "test@example.com";
        String password = "password123";
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setEmail(email);
        registerRequest.setPassword(password);
        userService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        AuthResponse authResponse = userService.login(loginRequest);

        assertNotNull(authResponse);
        assertNotNull(authResponse.getToken());
        assertNotNull(authResponse.getRefreshToken());
        assertEquals(Code.SUCCESS, authResponse.getCode());
    }

    @Test
    void Login_Fail_UserNotFound() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("nonexistent@example.com");
        loginRequest.setPassword("password123");

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.login(loginRequest);
        });

        assertEquals("User", exception.getResourceName());
        assertEquals("email", exception.getFieldName());
        assertEquals("nonexistent@example.com", exception.getFieldValue());
    }

    @Test
    void Login_Fail_InvalidCredentials() {
        String email = "test@example.com";
        String correctPassword = "password123";
        String incorrectPassword = "wrongpassword";
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setEmail(email);
        registerRequest.setPassword(correctPassword);
        userService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(incorrectPassword);

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            userService.login(loginRequest);
        });

        assertEquals("Invalid credentials", exception.getMessage());
        assertEquals("INVALID_CREDENTIALS", exception.getErrorCode());
    }

    //CHANGE PASSWORD
    @Test
    void changePassword_Success() {
        String email = "test@example.com";
        String password = "password123";
        UserRegisterRequest registerRequest = new UserRegisterRequest(email, password);
        AuthResponse responseRegister = userService.register(registerRequest);
        MockHttpServletRequest request = new MockHttpServletRequest();
        User user = userRepository.findUserByEmail(email).orElse(null);
        request.addHeader("userId", user.getUuid());
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("password123", "newPassword");

        AuthResponse response = userService.changePassword(changePasswordRequest, request);

        assertNotNull(response);
        assertEquals(Code.SUCCESS, response.getCode());

        User updatedUser = userRepository.findUserByEmail(email).orElse(null);
        assertNotNull(updatedUser);
        assertTrue(passwordEncoder.matches("newPassword", updatedUser.getPassword()));
    }

    @Test
    void validateToken_Fail_AccessTokenMissing() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("Refresh-Token", "Bearer validRefreshToken");

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            userService.validateToken(request, response);
        });

        assertEquals("Access token is missing or invalid", exception.getMessage());
        assertEquals("ACCESS_TOKEN_MISSING", exception.getErrorCode());
    }

    @Test
    void validateToken_Fail_AccessTokenExpired_RefreshTokenMissing() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        String expiredAccessToken = generateExpiredToken("test@example.com", "uuid123");
        request.addHeader("Authorization", "Bearer " + expiredAccessToken);

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            userService.validateToken(request, response);
        });

        assertEquals("Refresh token is missing or invalid.", exception.getMessage());
        assertEquals("REFRESH_TOKEN_MISSING", exception.getErrorCode());
    }

    @Test
    void validateToken_Success_BothTokensValid() {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        String validAccessToken = jwtService.generateToken("test@example.com", "uuid123", exp);
        String validRefreshToken = jwtService.generateToken("test@example.com", "uuid123", refreshExp);

        request.addHeader("Authorization", "Bearer " + validAccessToken);
        request.addHeader("Refresh-Token", "Bearer " + validRefreshToken);

        userService.validateToken(request, response);

        assertNull(response.getHeader("Authorization"));
        assertNull(response.getHeader("Refresh-Token"));
    }

    @Test
    void authorize_Success_AdminUser() {
        String email = "admin@example.com";
        String password = "password123";
        UserRegisterRequest registerRequest = new UserRegisterRequest(email, password);
        AuthResponse responseRegister = userService.register(registerRequest);

        User user = userRepository.findUserByEmail(email).orElseThrow();
        user.setRole(Role.ADMIN);
        userRepository.save(user);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + responseRegister.getToken());

        assertDoesNotThrow(() -> userService.authorize(request));
    }

    @Test
    void authorize_Fail_MissingAccessToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            userService.authorize(request);
        });

        assertEquals("Access token is missing or invalid.", exception.getMessage());
        assertEquals("ACCESS_TOKEN_MISSING", exception.getErrorCode());
    }

    @Test
    void authorize_Fail_InvalidTokenFormat() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalidToken");

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            userService.authorize(request);
        });

        assertEquals("Invalid token format.", exception.getMessage());
        assertEquals("INVALID_TOKEN_FORMAT", exception.getErrorCode());
    }

    @Test
    void authorize_Fail_UserNotFound() {
        String validToken = jwtService.generateToken("nonexistent@example.com", "uuid123", exp);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + validToken);

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            userService.authorize(request);
        });

        assertEquals("User not found.", exception.getMessage());
        assertEquals("USER_NOT_FOUND", exception.getErrorCode());
    }

    @Test
    void authorize_Fail_UserNotAdmin() {
        String email = "user@example.com";
        String password = "password123";
        UserRegisterRequest registerRequest = new UserRegisterRequest(email, password);
        AuthResponse responseRegister = userService.register(registerRequest);

        User user = userRepository.findUserByEmail(email).orElseThrow();
        user.setRole(Role.USER);
        userRepository.save(user);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + responseRegister.getToken());

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            userService.authorize(request);
        });

        assertEquals("Access denied. Admin role required.", exception.getMessage());
        assertEquals("ACCESS_DENIED", exception.getErrorCode());
    }

    private String generateExpiredToken(String email, String uuid) {
        return jwtService.generateToken(email, uuid, (int) -1000L); // Generate token with past expiration
    }


}
