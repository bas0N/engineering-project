package org.example.product.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.commondto.UserDetailInfoEvent;
import org.example.exception.exceptions.ErrorDetails;
import org.example.exception.exceptions.InvalidParameterException;
import org.example.exception.exceptions.ResourceNotFoundException;
import org.example.product.dto.Request.AddProductRequest;
import org.example.product.dto.Request.UpdateProductRequest;
import org.example.product.dto.Response.ImageUploadResponse;
import org.example.product.dto.Response.ProductDetailResponse;
import org.example.product.dto.Response.ProductResponse;
import org.example.product.entity.Image;
import org.example.product.entity.Product;
import org.example.product.entity.User;
import org.example.product.enums.DetailKey;
import org.example.product.repository.ProductRepository;
import org.example.product.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
//    @InjectMocks
//    private ProductServiceImpl productService;
//
//    @Mock
//    private MongoTemplate mongoTemplate;
//
//    @Mock
//    private ProductHistoryEventProducer productHistoryEventProducer;
//
//    @Mock
//    private ProductRepository productRepository;
//
//    @Mock
//    private Utils utils;
//
//    @Mock
//    private HttpServletRequest request;
//
//    @Mock
//    private UserService userService;
//
//    @Mock
//    private ImageService imageService;
//
//    @BeforeEach
//    void setUp() {
//        //MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void getProducts_Success() {
//        // Arrange
//        int page = 1;
//        int limit = 10;
//        String sort = "title";
//        String mainCategory = "Health and Personal Care";
//        String title = "Amazing Health Product";
//        Double minPrice = 10.0;
//        Double maxPrice = 20.0;
//        Double minRating = 4.0;
//        Double maxRating = 5.0;
//        List<String> categories = List.of("Health");
//        String store = "BestStore";
//
//        Product product = new Product(
//                null,
//                "user123",
//                "asin123",
//                List.of("Health", "Personal Care"),
//                List.of("A great product for daily use.", "Essential item."),
//                Map.of("Brand", "BrandX", "Model", "Model123"),
//                List.of("Feature1", "Feature2"),
//                List.of(new Image("thumbUrl", "largeUrl", "variantUrl", "hiResUrl")),
//                "Health and Personal Care",
//                "parent123",
//                "19.99",
//                150,
//                "BestStore",
//                "Amazing Health Product",
//                null,
//                4.7,
//                new User("test@gmail.com", "John", "Doe", "user123")
//        );
//
//        List<Product> mockProducts = List.of(product);
//
//        when(mongoTemplate.find(Mockito.any(Query.class), eq(Product.class))).thenReturn(mockProducts);
//
//        // Act
//        ResponseEntity<Page<ProductResponse>> response = productService.getProducts(
//                page, limit, sort, mainCategory, title, minPrice, maxPrice, minRating, maxRating, categories, store);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//
//        Page<ProductResponse> resultPage = response.getBody();
//        assertNotNull(resultPage);
//        assertEquals(1, resultPage.getTotalElements());
//
//        ProductResponse productResponse = resultPage.getContent().get(0);
//        assertEquals("Amazing Health Product", productResponse.getTitle());
//        assertEquals("Health and Personal Care", productResponse.getMainCategory());
//        assertEquals("19.99", productResponse.getPrice());
//        assertEquals(4.7, productResponse.getAverageRating());
//    }
//
//    @Test
//    void getProducts_NoFilters() {
//        // Arrange
//        int page = 1, limit = 10;
//
//        Product product = new Product();
//        product.setTitle("Default Product");
//        product.setMainCategory("Default Category");
//
//        List<Product> mockProducts = List.of(product);
//        when(mongoTemplate.find(any(Query.class), eq(Product.class))).thenReturn(mockProducts);
//
//        // Act
//        ResponseEntity<Page<ProductResponse>> response = productService.getProducts(
//                page, limit, null, null, null, null, null, null, null, null, null);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(200, response.getStatusCodeValue());
//        assertNotNull(response.getBody());
//        assertEquals(1, response.getBody().getTotalElements());
//    }
//
//    @Test
//    void getProductById_Success() {
//        // Arrange
//        String parentAsin = "parent123";
//        String userId = "user123";
//
//        Product product = new Product();
//        product.setParentAsin(parentAsin);
//        product.setUserId(userId);
//        product.setTitle("Amazing Health Product");
//        product.setMainCategory("Health and Personal Care");
//
//        when(productRepository.findByParentAsin(parentAsin)).thenReturn(Optional.of(product));
//        when(utils.getUserFromRequest(request)).thenReturn(userId);
//
//        // Act
//        ResponseEntity<?> response = productService.getProductById(parentAsin, request);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertInstanceOf(ProductDetailResponse.class, response.getBody());
//
//        ProductDetailResponse productResponse = (ProductDetailResponse) response.getBody();
//        assertEquals("Amazing Health Product", productResponse.getTitle());
//        assertEquals("Health and Personal Care", productResponse.getMainCategory());
//        verify(productHistoryEventProducer, times(1)).sendProductHistoryEvent(parentAsin, userId);
//    }
//
//    @Test
//    void getProductById_ProductNotFound() {
//        // Arrange
//        String parentAsin = "nonexistent123";
//        when(productRepository.findByParentAsin(parentAsin)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
//            productService.getProductById(parentAsin, request);
//        });
//
//        assertEquals("Product not found with parentAsin : 'nonexistent123'", thrown.getMessage());
//        verify(productHistoryEventProducer, never()).sendProductHistoryEvent(anyString(), anyString());
//    }
//
//    private AddProductRequest createAddProductRequest() {
//        AddProductRequest addProductRequest = new AddProductRequest();
//        addProductRequest.setCategories(List.of("Category1", "Category2"));
//        addProductRequest.setDescription(List.of("A great product.", "High-quality materials."));
//        addProductRequest.setDetails(Map.of(
//                DetailKey.BRAND, "BrandX",
//                DetailKey.AGE_RANGE, "ModelY123"
//        ));
//        addProductRequest.setFeatures(List.of("Feature1", "Feature2"));
//        addProductRequest.setMainCategory("MainCategory");
//        addProductRequest.setPrice("19.99");
//        addProductRequest.setStore("BestStore");
//        addProductRequest.setTitle("Amazing Product");
//        return addProductRequest;
//    }
//
//    @Test
//    void createProduct_Success() {
//        // Arrange
//        String userId = "user123";
//        AddProductRequest addProductRequest = createAddProductRequest();
//        UserDetailInfoEvent userInfo = new UserDetailInfoEvent("1", "email", "John", "Doe", "user123");
//
//        when(utils.getUserFromRequest(request)).thenReturn(userId);
//        when(userService.getUserDetailInfo(userId)).thenReturn(userInfo);
//        when(productRepository.save(any(Product.class))).thenReturn(new Product());
//
//        // Act
//        ResponseEntity<?> response = productService.createProduct(addProductRequest, request);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        verify(productRepository, times(1)).save(any(Product.class));
//    }
//
//    @Test
//    void updateProduct_Success() {
//        // Arrange
//        String productId = "product123";
//        String userId = "user123";
//        UpdateProductRequest updateProductRequest = new UpdateProductRequest();
//        updateProductRequest.setTitle("Updated Title");
//        updateProductRequest.setPrice("99.99");
//        updateProductRequest.setMainCategory("Updated Category");
//        updateProductRequest.setCategories(List.of("NewCategory1", "NewCategory2"));
//        updateProductRequest.setDescription(List.of("New description 1", "New description 2"));
//
//        Product existingProduct = new Product(
//                productId,
//                userId,
//                null,
//                List.of("Category1"),
//                List.of("Old Description"),
//                Map.of("Key1", "Value1"),
//                List.of("Feature1"),
//                null,
//                "MainCategory",
//                productId,
//                "49.99",
//                0,
//                "Store1",
//                "Old Title",
//                null,
//                0.0,
//                new User("email", "John", "Doe", userId)
//        );
//
//        when(productRepository.findByParentAsin(productId)).thenReturn(Optional.of(existingProduct));
//        when(utils.getUserFromRequest(request)).thenReturn(userId);
//        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        // Act
//        ResponseEntity<?> response = productService.updateProduct(productId, updateProductRequest, request);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertInstanceOf(ProductDetailResponse.class, response.getBody());
//
//        ProductDetailResponse productResponse = (ProductDetailResponse) response.getBody();
//        assertEquals("Updated Title", productResponse.getTitle());
//        assertEquals("99.99", productResponse.getPrice());
//        assertEquals("Updated Category", productResponse.getMainCategory());
//        assertEquals(List.of("NewCategory1", "NewCategory2"), productResponse.getCategories());
//        assertEquals(List.of("New description 1", "New description 2"), productResponse.getDescription());
//
//        verify(productRepository, times(1)).save(any(Product.class));
//    }
//
//    @Test
//    void updateProduct_ProductNotFound() {
//        // Arrange
//        String productId = "nonexistent123";
//        UpdateProductRequest updateProductRequest = new UpdateProductRequest();
//
//        when(productRepository.findByParentAsin(productId)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
//            productService.updateProduct(productId, updateProductRequest, request);
//        });
//
//        // Verify the exception details
//        assertNotNull(exception);
//        assertEquals("Product not found with ID : 'nonexistent123'", exception.getMessage());
//
//        // Verify that the save method is never called
//        verify(productRepository, never()).save(any(Product.class));
//    }
//
//    @Test
//    void deleteProduct_Success() {
//        String productId = "product123";
//        String userId = "user123";
//
//        Product product = new Product();
//        product.setUser(new User("test@gmail.com", "John", "Doe", userId));
//
//        when(utils.getUserFromRequest(request)).thenReturn(userId);
//        when(productRepository.findByParentAsin(productId)).thenReturn(Optional.of(product));
//
//        ResponseEntity<?> response = productService.deleteProduct(productId, request);
//
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Product with ID: " + productId + " has been successfully deleted.", response.getBody());
//
//        verify(productRepository, times(1)).deleteByParentAsin(productId);
//    }
//
//    @Test
//    void deleteProduct_ProductNotFound() {
//        // Arrange
//        String productId = "nonexistent123";
//
//        when(productRepository.findByParentAsin(productId)).thenReturn(Optional.empty());
//
//        // Act
//        ResponseEntity<?> response = productService.deleteProduct(productId, request);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertInstanceOf(ErrorDetails.class, response.getBody());
//
//        ErrorDetails errorDetails = (ErrorDetails) response.getBody();
//        assertTrue(errorDetails.getMessage().contains("Product not found"));
//
//        verify(productRepository, never()).deleteByParentAsin(anyString());
//    }
//
//    @Test
//    void addImageToProduct_Success() throws Exception {
//        // Arrange
//        String productId = "product123";
//        String userId = "user123";
//        MultipartFile hiRes = mock(MultipartFile.class);
//        MultipartFile large = mock(MultipartFile.class);
//        MultipartFile thumb = mock(MultipartFile.class);
//        String variant = "default";
//
//        Product product = new Product();
//        product.setUserId(userId);
//        product.setImages(new ArrayList<>());
//
//        ImageUploadResponse imageUploadResponse = new ImageUploadResponse(
//                "thumbUrl", "largeUrl", "variantUrl", "hiResUrl"
//        );
//
//        when(utils.getUserFromRequest(request)).thenReturn(userId);
//        when(productRepository.findByParentAsin(productId)).thenReturn(Optional.of(product));
//        when(imageService.addImage(eq(productId), eq(hiRes), eq(large), eq(thumb), eq(variant)))
//                .thenReturn(imageUploadResponse);
//
//        // Act
//        ResponseEntity<?> response = productService.addImageToProduct(productId, hiRes, large, thumb, variant, request);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(imageUploadResponse, response.getBody());
//
//        verify(productRepository, times(1)).save(any(Product.class));
//    }
//
//    @Test
//    void addImageToProduct_ProductNotFound() {
//        // Arrange
//        String productId = "nonexistent123";
//        MultipartFile hiRes = mock(MultipartFile.class);
//        MultipartFile large = mock(MultipartFile.class);
//        MultipartFile thumb = mock(MultipartFile.class);
//        String variant = "default";
//
//        when(productRepository.findByParentAsin(productId)).thenReturn(Optional.empty());
//
//        // Act
//        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
//            productService.addImageToProduct(productId, hiRes, large, thumb, variant, request);
//        });
//
//        // Assert
//        assertNotNull(exception);
//        assertEquals("Product not found with ID : 'nonexistent123'", exception.getMessage());
//
//        verify(productRepository, never()).save(any(Product.class));
//    }
//
//    @Test
//    void updateImage_Success() throws Exception {
//        // Arrange
//        String productId = "product123";
//        String userId = "user123";
//        int order = 1;
//        MultipartFile hiRes = mock(MultipartFile.class);
//        MultipartFile large = mock(MultipartFile.class);
//        MultipartFile thumb = mock(MultipartFile.class);
//        String variant = "default";
//
//        Product product = new Product();
//        product.setUserId(userId);
//        product.setImages(new ArrayList<>(List.of(
//                new Image("oldThumb", "oldLarge", "oldVariant", "oldHiRes")
//        )));
//
//        ImageUploadResponse imageUploadResponse = new ImageUploadResponse(
//                "newThumb", "newLarge", "newVariant", "newHiRes"
//        );
//
//        when(utils.getUserFromRequest(request)).thenReturn(userId);
//        when(productRepository.findByParentAsin(productId)).thenReturn(Optional.of(product));
//        when(imageService.addImage(eq(productId), eq(hiRes), eq(large), eq(thumb), eq(variant)))
//                .thenReturn(imageUploadResponse);
//
//        // Act
//        ResponseEntity<?> response = productService.updateImage(productId, hiRes, large, thumb, variant, order, request);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(imageUploadResponse, response.getBody());
//
//        verify(productRepository, times(1)).save(product);
//    }
//
//    @Test
//    void updateImage_InvalidOrderParameter() {
//        // Arrange
//        String productId = "product123";
//        int order = 0; // Invalid order
//        MultipartFile hiRes = mock(MultipartFile.class);
//        MultipartFile large = mock(MultipartFile.class);
//        MultipartFile thumb = mock(MultipartFile.class);
//        String variant = "default";
//
//        // Act & Assert
//        InvalidParameterException exception = assertThrows(InvalidParameterException.class, () -> {
//            productService.updateImage(productId, hiRes, large, thumb, variant, order, request);
//        });
//
//        // Assert
//        assertNotNull(exception);
//        assertEquals("Order parameter must be a positive integer starting from 1.", exception.getMessage());
//
//        // Verify that the product repository save method was not called
//        verify(productRepository, never()).save(any(Product.class));
//    }
//
//    @Test
//    void deleteImage_Success() {
//        // Arrange
//        String productId = "product123";
//        String userId = "user123";
//        int order = 1;
//
//        Product product = new Product();
//        product.setUserId(userId);
//        product.setImages(new ArrayList<>(List.of(
//                new Image("thumb1", "large1", "variant1", "hiRes1"),
//                new Image("thumb2", "large2", "variant2", "hiRes2")
//        )));
//
//        when(utils.getUserFromRequest(request)).thenReturn(userId);
//        when(productRepository.findByParentAsin(productId)).thenReturn(Optional.of(product));
//
//        // Act
//        ResponseEntity<?> response = productService.deleteImage(productId, order, request);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Image successfully deleted from product with ID: " + productId, response.getBody());
//
//        verify(productRepository, times(1)).save(product);
//        assertEquals(1, product.getImages().size());
//        assertEquals("thumb2", product.getImages().get(0).getThumb());
//    }
//
//    @Test
//    void deleteImage_InvalidOrderParameter() {
//        // Arrange
//        String productId = "product123";
//        int order = 0; // Invalid order
//
//        // Act
//        ResponseEntity<?> response = productService.deleteImage(productId, order, request);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertTrue(response.getBody() instanceof ErrorDetails);
//
//        ErrorDetails errorDetails = (ErrorDetails) response.getBody();
//        assertTrue(errorDetails.getMessage().contains("Order parameter must be a positive integer"));
//
//        verify(productRepository, never()).save(any(Product.class));
//    }
//
//    @Test
//    void getMyProducts_Success() {
//        // Arrange
//        String userId = "user123";
//        int page = 0, limit = 10;
//
//        Pageable pageable = PageRequest.of(page, limit);
//        Product product = new Product();
//        product.setId("product123");
//        product.setTitle("Sample Product");
//
//        Page<Product> productPage = new PageImpl<>(List.of(product), pageable, 1);
//
//        when(utils.getUserFromRequest(request)).thenReturn(userId);
//        when(productRepository.findByUserId(eq(userId), eq(pageable))).thenReturn(productPage);
//
//        // Act
//        ResponseEntity<?> response = productService.getMyProducts(request, page, limit);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertTrue(response.getBody() instanceof Page<?>);
//
//        Page<ProductResponse> responseBody = (Page<ProductResponse>) response.getBody();
//        assertEquals(1, responseBody.getTotalElements());
//        assertEquals("Sample Product", responseBody.getContent().get(0).getTitle());
//    }
//

}
