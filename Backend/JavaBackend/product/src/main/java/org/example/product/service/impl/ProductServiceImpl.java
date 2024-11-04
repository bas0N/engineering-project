package org.example.product.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.example.commondto.ProductHistoryEvent;
import org.example.exception.exceptions.ApiRequestException;
import org.example.jwtcommon.jwt.JwtCommonService;
import org.example.product.dto.Request.AddProductRequest;
import org.example.product.dto.Response.ProductResponse;
import org.example.product.entity.Image;
import org.example.product.entity.Product;
import org.example.product.mapper.ProductMapper;
import org.example.product.repository.ProductRepository;
import org.example.product.service.ImageService;
import org.example.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final KafkaTemplate<String, ProductHistoryEvent> productHistoryKafkaTemplate;
    private final JwtCommonService jwtCommonService;
    private final ImageService imageService;
    private final ProductMapper productMapper;

    public ResponseEntity<?> getProducts(int page, int limit, String sort, String mainCategory, String title, Double minPrice, Double maxPrice, Double minRating, Double maxRating, List<String> categories, String store) {
        try {
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
            Page<ProductResponse> productResponses = products.map(productMapper::toProductResponse);
            return ResponseEntity.ok(productResponses);
        } catch (DataAccessException e) {
            log.error("Database access error while retrieving products", e);
            throw new ApiRequestException("Error accessing the product database", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while retrieving products", e);
            throw new ApiRequestException("An unexpected error occurred while retrieving products", e.getMessage());
        }
    }

    public ResponseEntity<?> getProductById(String parentAsin) {
        try {
            Product product = productRepository.findByParentAsin(parentAsin).orElseThrow(() -> new ResourceNotFoundException("Product not found with parentAsin: " + parentAsin));
            ProductResponse productResponse = productMapper.toProductResponse(product);
            return ResponseEntity.ok(productResponse);
        } catch (ResourceNotFoundException e) {
            log.error("Product not found with ID: {}", parentAsin, e);
            throw e;
        } catch (DataAccessException e) {
            log.error("Database access error while retrieving product with ID: {}", parentAsin, e);
            throw new ApiRequestException("Error accessing the product database", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while retrieving product with ID: {}", parentAsin, e);
            throw new ApiRequestException("An unexpected error occurred while retrieving product with ID: " + parentAsin, e.getMessage());
        }
    }

    @Override
    public void visitProduct(String id) {
        try {
            Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
            ProductHistoryEvent productHistoryEvent = new ProductHistoryEvent(product.getId(), id);
            productHistoryKafkaTemplate.send("product-history-events", productHistoryEvent);
        } catch (ResourceNotFoundException e) {
            log.error("Product not found with ID: {}", id, e);
            throw e;
        } catch (DataAccessException e) {
            log.error("Database access error while updating product visits with ID: {}", id, e);
            throw new ApiRequestException("Error accessing the product database", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while updating product visits with ID: {}", id, e);
            throw new ApiRequestException("An unexpected error occurred while updating product visits with ID: " + id, e.getMessage());
        }
    }

    @Override
    public Product createProduct(AddProductRequest addProductRequest, HttpServletRequest request) {
        String token = jwtCommonService.getTokenFromRequest(request);
        String userId = jwtCommonService.getCurrentUserId(token);
        Map<String, String> detailsAsStringMap = addProductRequest.getDetails().entrySet()
                .stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().name(),
                        Map.Entry::getValue
                ));
        try {
            Product product = new Product(
                    null,
                    userId,
                    null,
                    addProductRequest.getCategories(),
                    addProductRequest.getDescription(),
                    detailsAsStringMap,
                    addProductRequest.getFeatures(),
                    imageService.uploadImages(addProductRequest.getImages()),
                    addProductRequest.getMainCategory(),
                    generateUuid(),
                    addProductRequest.getPrice(),
                    0,
                    addProductRequest.getStore(),
                    addProductRequest.getTitle(),
                    null,
                    0.0
            );
            return productRepository.save(product);
        } catch (DataAccessException e) {
            log.error("Database access error while creating product", e);
            throw new ApiRequestException("Error accessing the product database", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while creating product", e);
            throw new ApiRequestException("An unexpected error occurred while creating product", e.getMessage());
        }
    }

    @Override
    public Product updateProduct(String id, AddProductRequest addProductRequest, HttpServletRequest request) {
        productRepository.deleteByParentAsin(id);
        Optional<Product> productCheck = productRepository.findByParentAsin(id);
        if (productCheck.isPresent()) {
            throw new ApiRequestException("Product didnt deleted: " + id);
        }
        String token = jwtCommonService.getTokenFromRequest(request);
        String userId = jwtCommonService.getCurrentUserId(token);
        Map<String, String> detailsAsStringMap = addProductRequest.getDetails().entrySet()
                .stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().name(),
                        Map.Entry::getValue
                ));
        try {
            Product product = new Product(
                    null,
                    userId,
                    null,
                    addProductRequest.getCategories(),
                    addProductRequest.getDescription(),
                    detailsAsStringMap,
                    addProductRequest.getFeatures(),
                    imageService.uploadImages(addProductRequest.getImages()),
                    addProductRequest.getMainCategory(),
                    id,
                    addProductRequest.getPrice(),
                    0,
                    addProductRequest.getStore(),
                    addProductRequest.getTitle(),
                    null,
                    0.0
            );
            return productRepository.save(product);
        } catch (DataAccessException e) {
            log.error("Database access error while creating product", e);
            throw new ApiRequestException("Error accessing the product database", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while creating product", e);
            throw new ApiRequestException("An unexpected error occurred while creating product", e.getMessage());
        }
    }

    @Override
    public void deleteProduct(String id) {
        try {
            productRepository.deleteByParentAsin(id);
        } catch (DataAccessException e) {
            log.error("Database access error while deleting product with ID: {}", id, e);
            throw new ApiRequestException("Error accessing the product database", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while deleting product with ID: {}", id, e);
            throw new ApiRequestException("An unexpected error occurred while deleting product with ID: " + id, e.getMessage());
        }
    }

    private String generateUuid() {
        return UUID.randomUUID().toString();
    }
}
