package org.example.like.service;

import jakarta.transaction.Transactional;
import org.example.commonutils.Utils;
import org.example.exception.exceptions.InvalidParameterException;
import org.example.exception.exceptions.ResourceNotFoundException;
import org.example.exception.exceptions.UnauthorizedException;
import org.example.like.dto.IsLikeResponse;
import org.example.like.dto.ProductResponse;
import org.example.like.entity.Product;
import org.example.like.repository.LikeRepository;
import org.example.like.repository.ProductRepository;
import org.example.like.response.LikeResponse;
import org.example.like.service.impl.LikeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {
        "spring.kafka.bootstrap-servers=",
        "spring.kafka.listener.auto-startup=false"
})
@ActiveProfiles("test")
@Transactional
public class LikeServiceTest {
    @Autowired
    private LikeServiceImpl likeService;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private ProductRepository productRepository;

    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;

    @MockBean
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    private String productUuid;
    private String userUuid;

    @BeforeEach
    void setUp() {
        productUuid = "product-123";
        userUuid = "user-123";
    }

    @Test
    void testAddLike_Success() {
        // Arrange
        Product product = new Product();
        product.setUuid(productUuid);
        productRepository.saveAndFlush(product);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("userId", userUuid);

        // Act
        ResponseEntity<LikeResponse> response = likeService.addLike(productUuid, request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(productUuid, response.getBody().getProductId());
    }

    @Test
    void addLike_Failure_LikeAlreadyExists_GenericExceptionCheck() {
        // Arrange
        Product product = new Product();
        product.setUuid(productUuid);
        productRepository.saveAndFlush(product);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("userId", userUuid);

        // User likes the product once
        likeService.addLike(productUuid, request);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            // User tries to like the same product again
            likeService.addLike(productUuid, request);
        });

        // Assert that an exception was thrown (regardless of type)
        assertNotNull(exception);
    }


    @Test
    void testGetMyLikedProducts_Success() {
        // Arrange
        List<Product> productList = new ArrayList<>();
        Product product1 = new Product();
        product1.setUuid("product-1");
        productRepository.saveAndFlush(product1);

        Product product2 = new Product();
        product2.setUuid("product-2");
        productRepository.saveAndFlush(product2);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("userId", userUuid);


        likeService.addLike(product1.getUuid(), request);
        likeService.addLike(product2.getUuid(), request);


        // Act
        ResponseEntity<List<ProductResponse>> response = likeService.getMyLikedProducts(request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testGetNumberOfLikes_Success() {
        // Arrange
        Product product = new Product();
        product.setUuid(productUuid);
        productRepository.saveAndFlush(product);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("userId", userUuid);

        likeService.addLike(productUuid, request);

        // Act
        ResponseEntity<Long> response = likeService.getNumberOfLikes(productUuid);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody());
    }

    @Test
    void testRemoveLike_Success() {
        // Arrange
        Product product = new Product();
        product.setUuid(productUuid);
        productRepository.saveAndFlush(product);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("userId", userUuid);

        ResponseEntity<LikeResponse> response = likeService.addLike(productUuid, request);

        // Act
        ResponseEntity<String> removeResponse = likeService.removeLike(response.getBody().getLikeId(), request);

        // Assert
        assertNotNull(removeResponse);
        assertEquals(200, removeResponse.getStatusCodeValue());
        assertEquals("Like removed successfully", removeResponse.getBody());
    }

    @Test
    void removeLike_Failure_GenericExceptionCheck() {
        // Arrange
        String nonExistentLikeUuid = "non-existent-like";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("userId", userUuid);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            // Try to remove a non-existent like
            likeService.removeLike(nonExistentLikeUuid, request);
        });

        // Print exception details for debugging
        System.out.println("Exception thrown: " + exception.getClass().getName());
        System.out.println("Exception message: " + exception.getMessage());

        // Assert that an exception was thrown (regardless of type)
        assertNotNull(exception);
    }


    @Test
    void isLiked_Success() {
        // Arrange
        Product product = new Product();
        product.setUuid(productUuid);
        productRepository.saveAndFlush(product);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("userId", userUuid);

        ResponseEntity<LikeResponse> response = likeService.addLike(productUuid, request);

        // Act
        ResponseEntity<IsLikeResponse> isLikedResponse = likeService.isLiked(productUuid, request);

        // Assert
        assertNotNull(isLikedResponse);
    }
}
