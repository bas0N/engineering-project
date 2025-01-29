package org.example.basket.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.example.basket.dto.ListBasketItemDto;
import org.example.basket.dto.request.AddBasketItemRequest;
import org.example.basket.dto.request.DeleteItemRequest;
import org.example.basket.dto.response.BasketItemResponse;
import org.example.basket.entity.Basket;
import org.example.basket.entity.BasketItems;
import org.example.basket.repository.BasketItemRepository;
import org.example.basket.repository.BasketRepository;
import org.example.basket.service.impl.BasketServiceImpl;
import org.example.commondto.BasketProductEvent;
import org.example.commonutils.Utils;
import org.example.exception.exceptions.ResourceNotFoundException;
import org.example.exception.exceptions.UnExpectedError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = {
        "spring.kafka.bootstrap-servers=",
        "spring.kafka.listener.auto-startup=false"
})
@ActiveProfiles("test")
@Transactional
public class BasketServiceTest {
    @Autowired
    private BasketServiceImpl basketService;

    @Autowired
    private BasketRepository basketRepository;

    @Autowired
    private BasketItemRepository basketItemRepository;

    @MockBean
    private ProductService productService;

    @MockBean
    private Utils utils;

    @MockBean
    private HttpServletRequest request;

    @MockBean
    private HttpServletResponse response;

    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;

    @MockBean
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    private AddBasketItemRequest basketItemRequest;
    private DeleteItemRequest deleteItemRequest;
    private Basket basket;
    private BasketProductEvent product;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        basketItemRequest = new AddBasketItemRequest();
        basketItemRequest.setProduct("product-123");
        basketItemRequest.setQuantity(2);

        basket = new Basket();
        basket.setUuid(UUID.randomUUID().toString());
        basket.setOwnerId("user-123");
        basket = basketRepository.saveAndFlush(basket); // Persist the basket in H2

        product = new BasketProductEvent();
        product.setId("product-123");
        product.setPrice(100.0);
        product.setIsActive(true);

        deleteItemRequest = new DeleteItemRequest();
        deleteItemRequest.setBasketItemUuid(UUID.randomUUID().toString());
        deleteItemRequest.setQuantity(1L);

    }

    @Test
    void testAddProductToBasket_Success() throws ExecutionException, InterruptedException, TimeoutException {
        // Arrange
        when(utils.extractUserIdFromRequest(request)).thenReturn("user-123");
        when(productService.getProductById("product-123")).thenReturn(product); // Mock the ProductService call

        // Act
        ResponseEntity<BasketItemResponse> response = basketService.addProductToBasket(basketItemRequest, request, this.response);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("product-123", Objects.requireNonNull(response.getBody()).getProductId());

        // Verify item is saved to the H2 database
        BasketItems savedItem = basketItemRepository.findByBasketAndProduct(basket, "product-123").orElse(null);
        assertNotNull(savedItem);
        assertEquals(2, savedItem.getQuantity());
    }

    @Test
    void testAddProductToBasket_ProductNotFound() throws ExecutionException, InterruptedException, TimeoutException {
        // Arrange
        when(utils.extractUserIdFromRequest(request)).thenReturn("user-123");
        when(productService.getProductById("product-123")).thenThrow(new ResourceNotFoundException("Product", "ID", "product-123", "PRODUCT_NOT_FOUND", Map.of("productId", "product-123")));

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                basketService.addProductToBasket(basketItemRequest, request, this.response)
        );
        assertEquals("PRODUCT_NOT_FOUND", exception.getErrorCode());
    }

    @Test
    void testDeleteProductFromBasket_Success() {
        BasketItems basketItem = new BasketItems();
        basketItem.setUuid(deleteItemRequest.getBasketItemUuid());
        basketItem.setBasket(basket);
        basketItem.setQuantity(2L);
        basketItemRepository.saveAndFlush(basketItem);

        when(utils.extractUserIdFromRequest(request)).thenReturn("user-123");

        ResponseEntity<?> response = basketService.deleteProductFromBasket(deleteItemRequest, request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testDeleteProductFromBasket_ItemNotFound() {
        when(utils.extractUserIdFromRequest(request)).thenReturn("user-123");

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                basketService.deleteProductFromBasket(deleteItemRequest, request)
        );
        assertEquals("BASKET_ITEM_NOT_FOUND", exception.getErrorCode());
    }

    @Test
    void testGetItems_Success() throws ExecutionException, InterruptedException, TimeoutException {
        basketRepository.deleteAll();
        basketItemRepository.deleteAll();
        Basket basket = new Basket();
        basket.setUuid(UUID.randomUUID().toString());
        basket.setOwnerId("user-123");
        basket = basketRepository.saveAndFlush(basket);

        BasketItems basketItem = new BasketItems();
        basketItem.setUuid(UUID.randomUUID().toString());
        basketItem.setBasket(basket);
        basketItem.setQuantity(2L);
        basketItem.setProduct("product-123");
        basketItemRepository.saveAndFlush(basketItem);


        List<Basket> basketList = basketRepository.findAll();


        BasketProductEvent product = new BasketProductEvent();
        product.setId("product-123");
        product.setName("Product Name");
        product.setPrice(50.0);
        product.setIsActive(true);
        when(productService.getProductById("product-123")).thenReturn(product);
        when(utils.extractUserIdFromRequest(request)).thenReturn("user-123");

        ResponseEntity<ListBasketItemDto> response = basketService.getItems(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().getBasketProducts().size());
    }

}
