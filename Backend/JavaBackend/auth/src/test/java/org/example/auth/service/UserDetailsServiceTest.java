package org.example.auth.service;

import jakarta.transaction.Transactional;
import org.example.auth.dto.request.AddressRequest;
import org.example.auth.dto.request.AddressesChangeRequest;
import org.example.auth.dto.request.UserPersonalDataRequest;
import org.example.auth.dto.request.UserRegisterRequest;
import org.example.auth.dto.response.AuthResponse;
import org.example.auth.dto.response.ImageResponse;
import org.example.auth.dto.response.UserDetailsResponse;
import org.example.auth.entity.Address;
import org.example.auth.entity.Operation;
import org.example.auth.entity.User;
import org.example.auth.repository.AddressRepository;
import org.example.auth.repository.UserRepository;
import org.example.exception.exceptions.ResourceNotFoundException;
import org.example.exception.exceptions.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = {
        "spring.kafka.bootstrap-servers=",
        "spring.kafka.listener.auto-startup=false"
})
@ActiveProfiles("test")
@Transactional
public class UserDetailsServiceTest {
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private UserService userService;
//    @Autowired
//    private UserRepository userRepository;
    @SpyBean
    private UserRepository userRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @MockBean
    private ImageService imageService;
    @MockBean
    private CloudinaryService cloudinaryService;
    private static final int exp = 3600;
    private static final int refreshExp = 86400;


    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;

    @MockBean
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @BeforeEach
    void setUp() {
        addressRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void fillUserPersonalData_Success() {
        String email = "test@example.com";
        String password = "password123";
        UserRegisterRequest registerRequest = new UserRegisterRequest(email, password
        );
        AuthResponse responseRegister = userService.register(registerRequest);

        User user = userRepository.findUserByEmail(email).orElseThrow();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + responseRegister.getToken());

        UserPersonalDataRequest personalDataRequest = new UserPersonalDataRequest(
                "John",
                "Doe",
                "12345678901"
        );


        ResponseEntity<?> response = userDetailsService.fillUserPersonalData(personalDataRequest, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User personal data updated successfully", response.getBody());

        User updatedUser = userRepository.findUserByEmail(email).orElseThrow();
        assertEquals("John", updatedUser.getFirstName());
        assertEquals("Doe", updatedUser.getLastName());
        assertEquals("12345678901", updatedUser.getPhoneNumber());
    }

    @Test
    void fillUserPersonalData_Fail_UserNotFound() {
        String validTokenForNonExistentUser = jwtService.generateToken("nonexistent@example.com", "uuid-nonexistent", exp);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + validTokenForNonExistentUser);

        UserPersonalDataRequest personalDataRequest = new UserPersonalDataRequest(
                "John",
                "Doe",
                "12345678901"
        );

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userDetailsService.fillUserPersonalData(personalDataRequest, request);
        });

        assertEquals("User", exception.getResourceName());
        assertEquals("uuid", exception.getFieldName());
        assertEquals("uuid-nonexistent", exception.getFieldValue());
    }

    @Test
    void fillUserPersonalData_Success_PartialUpdate() {
        String email = "test@example.com";
        String password = "password123";
        UserRegisterRequest registerRequest = new UserRegisterRequest(email, password);
        AuthResponse responseRegister = userService.register(registerRequest);

        User user = userRepository.findUserByEmail(email).orElseThrow();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + responseRegister.getToken());

        UserPersonalDataRequest personalDataRequest = new UserPersonalDataRequest(
                "John",
                null,
                null
        );

        ResponseEntity<?> response = userDetailsService.fillUserPersonalData(personalDataRequest, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User personal data updated successfully", response.getBody());

        User updatedUser = userRepository.findUserByEmail(email).orElseThrow();
        assertEquals("John", updatedUser.getFirstName());
        assertNull(updatedUser.getLastName());
        assertNull(updatedUser.getPhoneNumber());
    }

    @Test
    void updateUserAddresses_Success_CreateUpdateDelete() {
        String email = "test@example.com";
        String password = "password123";
        UserRegisterRequest registerRequest = new UserRegisterRequest(email, password);
        AuthResponse authResponse = userService.register(registerRequest);

        User user = userRepository.findUserByEmail(email).orElseThrow();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + authResponse.getToken());

        // CREATE Address
        AddressRequest createAddressRequest = new AddressRequest(
                null, "123 Main St", "Springfield", "IL", "62701", "USA", Operation.CREATE
        );

        // UPDATE Address
        Address createdAddress = addressRepository.save(new Address(user, "Old St", "Old City", "Old State", "00000", "Old Country"));
        AddressRequest updateAddressRequest = new AddressRequest(
                createdAddress.getUuid(), "456 Elm St", "New Springfield", "IL", "62702", "USA", Operation.UPDATE
        );

        // DELETE Address
        Address addressToDelete = addressRepository.save(new Address(user, "Delete St", "Delete City", "Delete State", "11111", "USA"));
        AddressRequest deleteAddressRequest = new AddressRequest(
                addressToDelete.getUuid(), null, null, null, null, null, Operation.DELETE
        );

        AddressesChangeRequest addressesChangeRequest = new AddressesChangeRequest(
                List.of(createAddressRequest, updateAddressRequest, deleteAddressRequest)
        );


        ResponseEntity<?> response = userDetailsService.updateUserAddresses(addressesChangeRequest, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Addresses updated successfully", response.getBody());
        List<Address> updatedAddresses = addressRepository.findAllByUser(user);
        assertTrue(updatedAddresses.stream().anyMatch(address ->
                "123 Main St".equals(address.getStreet()) &&
                        "Springfield".equals(address.getCity()) &&
                        "IL".equals(address.getState()) &&
                        "62701".equals(address.getPostalCode()) &&
                        "USA".equals(address.getCountry())
        ));

//        assertTrue(updatedAddresses.stream().anyMatch(address ->
//                "456 Elm St".equals(address.getStreet()) &&
//                        "New Springfield".equals(address.getCity()) &&
//                        "IL".equals(address.getState()) &&
//                        "62702".equals(address.getPostalCode()) &&
//                        "USA".equals(address.getCountry())
//        ));

        assertFalse(updatedAddresses.stream().anyMatch(address ->
                "Delete St".equals(address.getStreet())
        ));
    }

    @Test
    void updateUserAddresses_Fail_ResourceNotFound_AddressNotFoundForUpdate() {
        // Arrange
        String email = "test@example.com";
        String password = "password123";
        UserRegisterRequest registerRequest = new UserRegisterRequest(email, password);
        AuthResponse authResponse = userService.register(registerRequest);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + authResponse.getToken());

        AddressRequest updateAddressRequest = new AddressRequest(
                "nonexistent-uuid", "456 Elm St", "Springfield", "IL", "62702", "USA", Operation.UPDATE
        );

        AddressesChangeRequest addressesChangeRequest = new AddressesChangeRequest(List.of(updateAddressRequest));

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userDetailsService.updateUserAddresses(addressesChangeRequest, request);
        });

        assertEquals("Address", exception.getResourceName());
        assertEquals("uuid", exception.getFieldName());
        assertEquals("nonexistent-uuid", exception.getFieldValue());
    }

    @Test
    void updateUserAddresses_Fail_UnsupportedOperation() {
        // Arrange
        String email = "test@example.com";
        String password = "password123";
        UserRegisterRequest registerRequest = new UserRegisterRequest(email, password);
        AuthResponse authResponse = userService.register(registerRequest);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + authResponse.getToken());

        AddressRequest unsupportedOperationRequest = new AddressRequest(
                null, "456 Elm St", "Springfield", "IL", "62702", "USA", null
        );

        AddressesChangeRequest addressesChangeRequest = new AddressesChangeRequest(List.of(unsupportedOperationRequest));

        // Act & Assert
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> {
            userDetailsService.updateUserAddresses(addressesChangeRequest, request);
        });

        assertEquals("Operation not supported: null", exception.getMessage());
    }

    @Test
    void uploadImage_Success_NewImage() throws Exception {
        String email = "test@example.com";
        String password = "password123";
        UserRegisterRequest registerRequest = new UserRegisterRequest(email, password);
        AuthResponse responseRegister = userService.register(registerRequest);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + responseRegister.getToken());

        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("test-image.jpg");
        when(mockFile.getBytes()).thenReturn(new byte[]{1, 2, 3}); // Mock file content

        String mockImageUrl = "https://example.com/new-image.jpg";
        when(cloudinaryService.uploadImage(any(File.class))).thenReturn(mockImageUrl);

        ResponseEntity<?> response = userDetailsService.uploadImage(request, mockFile);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(ImageResponse.class, response.getBody());
    }

    @Test
    void deleteImage_Success() throws Exception {
        // Arrange
        String email = "test@example.com";
        String password = "password123";
        UserRegisterRequest registerRequest = new UserRegisterRequest(email, password);
        AuthResponse responseRegister = userService.register(registerRequest);

        User user = userRepository.findUserByEmail(email).orElseThrow();
        user.setImageUrl("https://example.com/image.jpg");
        userRepository.save(user);
        userRepository.flush();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + responseRegister.getToken());

        // Act
        ResponseEntity<?> response = userDetailsService.deleteImage(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Image deleted successfully", response.getBody());

        User updatedUser = userRepository.findUserByEmail(email).orElseThrow();

        verify(imageService, times(1)).deleteImage("https://example.com/image.jpg");
        verify(userRepository, times(1)).updateImageUrlByUuid(user.getUuid(), null);
    }

    @Test
    void getUserDetailsByUUid_Success() {
        // Arrange
        String userUuid = "user-uuid-123";
        User mockUser = new User();
        mockUser.setUuid(userUuid);
        mockUser.setEmail("test@example.com");
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");

        //when(userRepository.findByUuid(userUuid)).thenReturn(Optional.of(mockUser));
        userRepository.save(mockUser);

        // Act
        ResponseEntity<?> response = userDetailsService.getUserDetailsByUUid(userUuid);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(UserDetailsResponse.class, response.getBody());

        UserDetailsResponse userDetailsResponse = (UserDetailsResponse) response.getBody();
        assertEquals("John", userDetailsResponse.getFirstName());
        assertEquals("Doe", userDetailsResponse.getLastName());
        assertEquals("test@example.com", userDetailsResponse.getEmail());
        assertEquals(userUuid, userDetailsResponse.getId());

        verify(userRepository, times(1)).findByUuid(userUuid);
    }

    @Test
    void getUserDetailsByUUid_Fail_UserNotFound() {
        // Arrange
        String userUuid = "non-existent-uuid";
        when(userRepository.findByUuid(userUuid)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userDetailsService.getUserDetailsByUUid(userUuid);
        });

        assertEquals("User", exception.getResourceName());
        assertEquals("uuid", exception.getFieldName());
        assertEquals(userUuid, exception.getFieldValue());

        verify(userRepository, times(1)).findByUuid(userUuid);
    }

    @Test
    void getUserDetails_Success() {
        // Arrange
        String userUuid = "user-uuid-123";
        MockHttpServletRequest request = new MockHttpServletRequest();

        User mockUser = new User();
        mockUser.setUuid(userUuid);
        String email = "test@example.com";
        mockUser.setEmail(email);
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        request.addHeader("Authorization", "Bearer " + jwtService.generateToken(email, userUuid, exp));

        userRepository.save(mockUser);


        // Act
        ResponseEntity<?> response = userDetailsService.getUserDetails(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof UserDetailsResponse);

        UserDetailsResponse userDetailsResponse = (UserDetailsResponse) response.getBody();
        assertEquals("John", userDetailsResponse.getFirstName());
        assertEquals("Doe", userDetailsResponse.getLastName());
        assertEquals("test@example.com", userDetailsResponse.getEmail());
        assertEquals(userUuid, userDetailsResponse.getId());
    }
}
