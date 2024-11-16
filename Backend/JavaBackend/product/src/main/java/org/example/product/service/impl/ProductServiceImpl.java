package org.example.product.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.example.commondto.UserDetailInfoEvent;
import org.example.exception.exceptions.*;
import org.example.jwtcommon.jwt.JwtCommonService;
import org.example.product.dto.Request.AddProductRequest;
import org.example.product.dto.Request.ImageRequest;
import org.example.product.dto.Response.ProductResponse;
import org.example.product.entity.Product;
import org.example.product.entity.User;
import org.example.product.kafka.history.ProductHistoryEventProducer;
import org.example.product.kafka.user.UserEventConsumer;
import org.example.product.kafka.user.UserEventProducer;
import org.example.product.mapper.ProductMapper;
import org.example.product.mapper.UserMapper;
import org.example.product.repository.ProductRepository;
import org.example.product.service.ImageService;
import org.example.product.service.ProductService;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final JwtCommonService jwtCommonService;
    private final ImageService imageService;
    private final UserEventProducer userEventProducer;
    private final UserEventConsumer userEventConsumer;
    private final ProductHistoryEventProducer productHistoryEventProducer;

    public ResponseEntity<Page<ProductResponse>> getProducts(int page, int limit, String sort, String mainCategory, String title, Double minPrice, Double maxPrice, Double minRating, Double maxRating, List<String> categories, String store) {
        try {
            validateRequestParameters(page, limit, minPrice, maxPrice, minRating, maxRating);

            PageRequest pageRequest = PageRequest.of(page - 1, limit, Sort.by(sort).ascending());
            Page<Product> products;

            if (mainCategory != null || title != null || minPrice != null || maxPrice != null ||
                    minRating != null || maxRating != null || categories != null || store != null) {

                products = productRepository.searchProducts(
                        mainCategory,
                        title != null ? ".*" + title + ".*" : null,
                        minPrice != null ? minPrice : Double.MIN_VALUE,
                        maxPrice != null ? maxPrice : Double.MAX_VALUE,
                        minRating != null ? minRating : 0.0,
                        maxRating != null ? maxRating : 5.0,
                        categories,
                        store,
                        pageRequest
                );
            } else {
                products = productRepository.findAll(pageRequest);
            }
            return ResponseEntity.ok(products.map(ProductMapper.INSTANCE::toProductResponse));
        } catch (InvalidParameterException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<ProductResponse> getProductById(String parentAsin, HttpServletRequest request) {
        try {
            Product product = productRepository.findByParentAsin(parentAsin)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with parentAsin: " + parentAsin));

            String userId = jwtCommonService.getUserFromRequest(request);

            productHistoryEventProducer.sendProductHistoryEvent(product.getParentAsin(), userId);

            ProductResponse productResponse = ProductMapper.INSTANCE.toProductResponse(product);
            return ResponseEntity.ok(productResponse);
        } catch (ResourceNotFoundException e) {
            log.error("Product not found with ID: {}", parentAsin, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        } catch (DataAccessException e) {
            log.error("Database access error while retrieving product with ID: {}", parentAsin, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        } catch (Exception e) {
            log.error("Unexpected error while retrieving product with ID: {}", parentAsin, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @Override
    public Product createProduct(@Valid AddProductRequest addProductRequest, HttpServletRequest request) {
        try {
            String userId = jwtCommonService.getUserFromRequest(request);

            userEventProducer.sendUserEvent(userId);
            CompletableFuture<UserDetailInfoEvent> userFuture = userEventConsumer.getUserDetails(userId)
                    .orTimeout(30, TimeUnit.SECONDS)
                    .exceptionally(ex -> {
                        log.error("Failed to retrieve user details for userId: {}", userId, ex);
                        throw new ApiRequestException("Could not retrieve user details");
                    });

            UserDetailInfoEvent userInfo = userFuture.join();

            Map<String, String> detailsAsStringMap = addProductRequest.getDetails().entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                            entry -> entry.getKey().name(),
                            Map.Entry::getValue
                    ));

            Product product = new Product(
                    null,
                    userId,
                    null,
                    addProductRequest.getCategories(),
                    addProductRequest.getDescription(),
                    detailsAsStringMap,
                    addProductRequest.getFeatures(),
                   // imageService.uploadImages(imageRequests), // Obsługa uploadu obrazów
                    null,
                    addProductRequest.getMainCategory(),
                    generateUuid(),
                    addProductRequest.getPrice(),
                    0,
                    addProductRequest.getStore(),
                    addProductRequest.getTitle(),
                    null,
                    0.0,
                    UserMapper.INSTANCE.toUser(userInfo)
            );

            return productRepository.save(product);
        } catch (DataAccessException e) {
            log.error("Database access error while creating product", e);
            throw new DatabaseAccessException("Error accessing the product database", e);
        } catch (Exception e) {
            log.error("Unexpected error while creating product", e);
            throw new ApiRequestException("An unexpected error occurred while creating product", e.getMessage());
        }
    }


    @Override
    public Product updateProduct(String id, AddProductRequest addProductRequest, HttpServletRequest request) {
        Product existingProduct = productRepository.findByParentAsin(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        String userId = jwtCommonService.getUserFromRequest(request);

        if (userId != null && !userId.equals(existingProduct.getUserId())) {
            throw new UnauthorizedException("You are not authorized to update this product", "UNAUTHORIZED");
        }

        Map<String, String> detailsAsStringMap = addProductRequest.getDetails().entrySet()
                .stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().name(),
                        Map.Entry::getValue
                ));
        try {
            if (!existingProduct.getCategories().isEmpty()) {
                existingProduct.setCategories(addProductRequest.getCategories());
            }
            if (addProductRequest.getDescription() != null) {
                existingProduct.setDescription(addProductRequest.getDescription());
            }
            if (!detailsAsStringMap.isEmpty()) {
                existingProduct.setDetails(detailsAsStringMap);
            }
            if (addProductRequest.getFeatures() != null) {
                existingProduct.setFeatures(addProductRequest.getFeatures());
            }
//            if (addProductRequest.getImages() != null) {
//                existingProduct.setImages(imageService.uploadImages(addProductRequest.getImages()));
//            }
            if (addProductRequest.getMainCategory() != null) {
                existingProduct.setMainCategory(addProductRequest.getMainCategory());
            }
            if (addProductRequest.getPrice() != null) {
                existingProduct.setPrice(addProductRequest.getPrice());
            }
            if (addProductRequest.getStore() != null) {
                existingProduct.setStore(addProductRequest.getStore());
            }
            if (addProductRequest.getTitle() != null) {
                existingProduct.setTitle(addProductRequest.getTitle());
            }
            return productRepository.save(existingProduct);
        } catch (DataAccessException e) {
            log.error("Database access error while creating product", e);
            throw new ApiRequestException("Error accessing the product database", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while creating product", e);
            throw new ApiRequestException("An unexpected error occurred while creating product", e.getMessage());
        }
    }

    @Override
    public void deleteProduct(String id, HttpServletRequest request) {
        try {
            String userId = jwtCommonService.getUserFromRequest(request);
            User owner = productRepository.findByParentAsin(id).isPresent() ? productRepository.findByParentAsin(id).get().getUser() : null;
            if(userId == null || owner == null || !userId.equals(owner.getUserId())) {
                throw new UnauthorizedException("You are not authorized to delete this product", "UNAUTHORIZED");
            }
            boolean exists = productRepository.existsByParentAsin(id).isPresent();
            if (!exists) {
                throw new ResourceNotFoundException("Product not found with ID: " + id);
            }
            productRepository.deleteByParentAsin(id);
        } catch (DataAccessException e) {
            log.error("Database access error while deleting product with ID: {}", id, e);
            throw new DatabaseAccessException("Error accessing the product database", e);
        } catch (Exception e) {
            log.error("Unexpected error while deleting product with ID: {}", id, e);
            throw new ApiRequestException("An unexpected error occurred while deleting product with ID: " + id, e.getMessage());
        }
    }

    private String generateUuid() {
        return UUID.randomUUID().toString();
    }

    private void validateRequestParameters(int page, int limit, Double minPrice, Double maxPrice, Double minRating, Double maxRating) {
        if (page < 1 || limit < 1) {
            throw new InvalidParameterException("Page and limit parameters must be positive.");
        }
        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw new InvalidParameterException("Minimum price cannot be greater than maximum price.");
        }
        if (minRating != null && maxRating != null && minRating > maxRating) {
            throw new InvalidParameterException("Minimum rating cannot be greater than maximum rating.");
        }
    }

}
