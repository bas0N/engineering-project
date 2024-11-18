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
import org.example.product.dto.Request.UpdateProductRequest;
import org.example.product.dto.Response.ImageUploadResponse;
import org.example.product.dto.Response.ProductResponse;
import org.example.product.entity.Image;
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
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.MongoTemplate;
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
    private final MongoTemplate mongoTemplate;

    public ResponseEntity<Page<ProductResponse>> getProducts(int page, int limit, String sort, String mainCategory, String title, Double minPrice, Double maxPrice, Double minRating, Double maxRating, List<String> categories, String store) {
        try {
            validateRequestParameters(page, limit, minPrice, maxPrice, minRating, maxRating);
            Query query = new Query();

            if (mainCategory != null) {
                query.addCriteria(Criteria.where("mainCategory").is(mainCategory));
            }
            if (title != null) {
                query.addCriteria(Criteria.where("title").regex(".*" + title + ".*", "i"));
            }
            query.addCriteria(Criteria.where("price").gte(minPrice != null ? minPrice : 0.0)
                    .lte(maxPrice != null ? maxPrice : Double.MAX_VALUE));
            query.addCriteria(Criteria.where("averageRating").gte(minRating != null ? minRating : 0.0)
                    .lte(maxRating != null ? maxRating : 5.0));
            if (categories != null && !categories.isEmpty()) {
                query.addCriteria(Criteria.where("categories").in(categories));
            }
            if (store != null) {
                query.addCriteria(Criteria.where("store").is(store));
            }

            Pageable pageable = Pageable.ofSize(limit).withPage(page - 1);
            query.with(pageable);

            List<Product> products = mongoTemplate.find(query, Product.class);

            List<ProductResponse> productResponses = products.stream()
                    .map(ProductMapper.INSTANCE::toProductResponse)
                    .toList();

            return ResponseEntity.ok(new PageImpl<>(productResponses, pageable, products.size()));
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
    public ProductResponse createProduct(AddProductRequest addProductRequest, HttpServletRequest request) {
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

            Product savedProduct = productRepository.save(product);
            return ProductMapper.INSTANCE.toProductResponse(savedProduct);
        } catch (DataAccessException e) {
            log.error("Database access error while creating product", e);
            throw new DatabaseAccessException("Error accessing the product database", e);
        } catch (Exception e) {
            log.error("Unexpected error while creating product", e);
            throw new ApiRequestException("An unexpected error occurred while creating product", e.getMessage());
        }
    }


    @Override
    public ProductResponse updateProduct(String id, UpdateProductRequest updateProductRequest, HttpServletRequest request) {
        Product existingProduct = productRepository.findByParentAsin(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        String userId = jwtCommonService.getUserFromRequest(request);

        if (userId != null && !userId.equals(existingProduct.getUserId())) {
            throw new UnauthorizedException("You are not authorized to update this product", "UNAUTHORIZED");
        }

        Map<String, String> detailsAsStringMap = new HashMap<>();
        if (updateProductRequest.getDetails()!=null && !updateProductRequest.getDetails().isEmpty()) {
            detailsAsStringMap = updateProductRequest.getDetails().entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                            entry -> entry.getKey().name(),
                            Map.Entry::getValue
                    ));
        }

        try {
            if (!existingProduct.getCategories().isEmpty() && updateProductRequest.getCategories() != null) {
                existingProduct.setCategories(updateProductRequest.getCategories());
            }
            if (updateProductRequest.getDescription() != null) {
                existingProduct.setDescription(updateProductRequest.getDescription());
            }
            if (!detailsAsStringMap.isEmpty()) {
                existingProduct.setDetails(detailsAsStringMap);
            }
            if (updateProductRequest.getFeatures() != null) {
                existingProduct.setFeatures(updateProductRequest.getFeatures());
            }
            if (updateProductRequest.getMainCategory() != null) {
                existingProduct.setMainCategory(updateProductRequest.getMainCategory());
            }
            if (updateProductRequest.getPrice() != null) {
                existingProduct.setPrice(updateProductRequest.getPrice());
            }
            if (updateProductRequest.getStore() != null) {
                existingProduct.setStore(updateProductRequest.getStore());
            }
            if (updateProductRequest.getTitle() != null) {
                existingProduct.setTitle(updateProductRequest.getTitle());
            }
            Product savedProduct = productRepository.save(existingProduct);
            return ProductMapper.INSTANCE.toProductResponse(savedProduct);
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
            if (userId == null || owner == null || !userId.equals(owner.getUserId())) {
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

    @Override
    public ResponseEntity<?> addImageToProduct(String productId, MultipartFile hi_Res, MultipartFile large, MultipartFile thumb, String variant, HttpServletRequest request) {
        try {
            String userId = jwtCommonService.getUserFromRequest(request);
            Product product = productRepository.findByParentAsin(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

            if (!userId.equals(product.getUserId())) {
                throw new UnauthorizedException("You are not authorized to upload images for this product", "UNAUTHORIZED");
            }

            ImageUploadResponse imageUploadResponse = imageService.addImage(productId, hi_Res, large, thumb, variant);
            Image image = new Image(
                    imageUploadResponse.getThumb(),
                    imageUploadResponse.getLarge(),
                    imageUploadResponse.getVariant(),
                    imageUploadResponse.getHiRes()
            );
            List<Image> images = product.getImages();
            if (images == null) {
                images = new ArrayList<>();
            }
            images.add(image);
            product.setImages(images);
            productRepository.save(product);
            return ResponseEntity.ok(imageUploadResponse);
        } catch (ResourceNotFoundException e) {
            log.error("Product not found with ID: {}", productId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (UnauthorizedException e) {
            log.error("Unauthorized access to product with ID: {}", productId, e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (Exception e) {
            log.error("Unexpected error while uploading images for product with ID: {}", productId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Override
    public ResponseEntity<?> updateImage(String productId, MultipartFile hiRes, MultipartFile large, MultipartFile thumb, String variant, int order, HttpServletRequest request) {
        try {
            if (order < 0) {
                throw new InvalidParameterException("Order parameter must be positive.");
            }
            String userId = jwtCommonService.getUserFromRequest(request);
            Product product = productRepository.findByParentAsin(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

            if (!userId.equals(product.getUserId())) {
                throw new UnauthorizedException("You are not authorized to upload images for this product", "UNAUTHORIZED");
            }

            ImageUploadResponse imageUploadResponse = imageService.addImage(productId, hiRes, large, thumb, variant);
            List<Image> images = product.getImages();
            if (images == null || order > images.size()) {
                throw new ResourceNotFoundException("No images found for product with ID or order is bigger than list size: " + productId);
            }
            Image image = new Image(
                    imageUploadResponse.getThumb(),
                    imageUploadResponse.getLarge(),
                    imageUploadResponse.getVariant(),
                    imageUploadResponse.getHiRes()
            );
            images.set(order - 1, image);
            product.setImages(images);
            productRepository.save(product);
            return ResponseEntity.ok(imageUploadResponse);
        } catch (ResourceNotFoundException e) {
            log.error("Product not found with ID: {}", productId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (UnauthorizedException e) {
            log.error("Unauthorized access to product with ID: {}", productId, e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (Exception e) {
            log.error("Unexpected error while uploading images for product with ID: {}", productId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Override
    public void deleteImage(String productId, int order, HttpServletRequest request) {
        try {
            if (order < 0) {
                throw new InvalidParameterException("Order parameter must be positive.");
            }
            String userId = jwtCommonService.getUserFromRequest(request);
            Product product = productRepository.findByParentAsin(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

            if (!userId.equals(product.getUserId())) {
                throw new UnauthorizedException("You are not authorized to delete images for this product", "UNAUTHORIZED");
            }

            List<Image> images = product.getImages();
            if (images == null || order >= images.size()) {
                throw new ResourceNotFoundException("No images found for product with ID or order is bigger than list size: " + productId);
            }
            images.remove(order - 1);
            product.setImages(images);
            productRepository.save(product);
        } catch (ResourceNotFoundException e) {
            log.error("Product not found with ID: {}", productId, e);
            throw e;
        } catch (UnauthorizedException e) {
            log.error("Unauthorized access to product with ID: {}", productId, e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while deleting images for product with ID: {}", productId, e);
            throw new ApiRequestException("An unexpected error occurred while deleting images for product with ID: " + productId, e.getMessage());
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
