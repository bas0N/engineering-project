package org.example.product.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.commondto.UserDetailInfoEvent;
import org.example.exception.exceptions.*;
import org.example.jwtcommon.jwt.JwtCommonService;
import org.example.product.dto.Request.AddProductRequest;
import org.example.product.dto.Request.ImageRequest;
import org.example.product.dto.Request.UpdateProductRequest;
import org.example.product.dto.Response.ImageUploadResponse;
import org.example.product.dto.Response.ProductDetailResponse;
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
import org.example.product.service.UserService;
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
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final JwtCommonService jwtCommonService;
    private final ImageService imageService;
    private final ProductHistoryEventProducer productHistoryEventProducer;
    private final MongoTemplate mongoTemplate;
    private final UserService userService;

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
                query.addCriteria(Criteria.where("store").regex(".*" + store + ".*", "i"));
            }

            Pageable pageable = Pageable.ofSize(limit).withPage(page - 1);
            query.with(pageable);

            List<Product> products = mongoTemplate.find(query, Product.class);

            if (products.isEmpty()) {
                throw new ResourceNotFoundException("Product", "query", query.toString());
            }

            List<ProductResponse> productResponses = products.stream()
                    .map(ProductMapper.INSTANCE::toProductResponse)
                    .toList();

            return ResponseEntity.ok(new PageImpl<>(productResponses, pageable, products.size()));
        } catch (InvalidParameterException e) {
            log.error("Invalid parameters provided: {}", e.getMessage());
            throw new InvalidParameterException(e.getMessage());
        } catch (DatabaseAccessException e) {
            log.error("Database access error while retrieving products", e);
            throw new DatabaseAccessException("Error accessing the product database", e);
        } catch (Exception e) {
            log.error("Unexpected error while retrieving products", e);
            throw new ApiRequestException("An unexpected error occurred while retrieving products", e.getMessage());
        }
    }

    public ResponseEntity<ProductDetailResponse> getProductById(String parentAsin, HttpServletRequest request) {
        try {
            Product product = productRepository.findByParentAsin(parentAsin)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "parentAsin", parentAsin));

            String userId = jwtCommonService.getUserFromRequest(request);

            productHistoryEventProducer.sendProductHistoryEvent(product.getParentAsin(), userId);

            ProductDetailResponse productResponse = ProductMapper.INSTANCE.toProductDetailResponse(product);

            return ResponseEntity.ok(productResponse);
        } catch (ResourceNotFoundException e) {
            log.error("Product not found with parentAsin: {}", parentAsin, e);
            throw new ResourceNotFoundException("Product", "parentAsin", parentAsin);
        } catch (DataAccessException e) {
            log.error("Database access error while retrieving product with parentAsin: {}", parentAsin, e);
            throw new DatabaseAccessException("Error accessing the product database while retrieving product with parentAsin: " + parentAsin, e);
        } catch (Exception e) {
            log.error("Unexpected error while retrieving product with parentAsin: {}", parentAsin, e);
            throw new ApiRequestException("An unexpected error occurred while retrieving product with parentAsin: " + parentAsin, e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ProductDetailResponse> createProduct(AddProductRequest addProductRequest, HttpServletRequest request) {
        try {
            String userId = jwtCommonService.getUserFromRequest(request);

            UserDetailInfoEvent userInfo = userService.getUserDetailInfo(userId);

            if (userInfo == null) {
                throw new ResourceNotFoundException("User", "ID", userId);
            }

            if (userInfo.getFirstName() == null || userInfo.getLastName() == null || userInfo.getEmail() == null) {
                throw new MissingUserDetailsException(
                        "User must complete profile information. Missing details: " +
                                (userInfo.getFirstName() == null ? "First Name, " : "") +
                                (userInfo.getLastName() == null ? "Last Name, " : "") +
                                (userInfo.getEmail() == null ? "Email" : "")
                );
            }

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

            ProductDetailResponse productResponse = ProductMapper.INSTANCE.toProductDetailResponse(savedProduct);
            return ResponseEntity.ok(productResponse);
        } catch (MissingUserDetailsException e) {
            log.error("Missing user details: {}", e.getMessage());
            throw new MissingUserDetailsException(e.getMessage());
        } catch (DataAccessException e) {
            log.error("Database access error while creating product", e);
            throw new DatabaseAccessException("Error accessing the product database while creating product", e);
        } catch (Exception e) {
            log.error("Unexpected error while creating product", e);
            throw new ApiRequestException("An unexpected error occurred while creating product", e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ProductDetailResponse> updateProduct(String id, UpdateProductRequest updateProductRequest, HttpServletRequest request) {
        try {
            Product existingProduct = productRepository.findByParentAsin(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "ID", id));

            String userId = jwtCommonService.getUserFromRequest(request);

            if (userId != null && !userId.equals(existingProduct.getUserId())) {
                throw new UnauthorizedException("You are not authorized to update this product", "UNAUTHORIZED");
            }

            Map<String, String> detailsAsStringMap = new HashMap<>();
            if (updateProductRequest.getDetails() != null && !updateProductRequest.getDetails().isEmpty()) {
                detailsAsStringMap = updateProductRequest.getDetails().entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                                entry -> entry.getKey().name(),
                                Map.Entry::getValue
                        ));
            }

            if (updateProductRequest.getCategories() != null) {
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

            ProductDetailResponse productResponse = ProductMapper.INSTANCE.toProductDetailResponse(savedProduct);
            return ResponseEntity.ok(productResponse);
        } catch (ResourceNotFoundException e) {
            log.error("Product not found: {}", e.getMessage());
            throw new ResourceNotFoundException("Product", "ID", id);
        } catch (UnauthorizedException e) {
            log.error("Unauthorized access: {}", e.getMessage());
            throw new UnauthorizedException("You are not authorized to update this product", "UNAUTHORIZED");
        } catch (DataAccessException e) {
            log.error("Database access error while updating product", e);
            throw new DatabaseAccessException("Error accessing the product database while updating product", e);
        } catch (Exception e) {
            log.error("Unexpected error while updating product", e);
            throw new ApiRequestException("An unexpected error occurred while updating product", e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> deleteProduct(String id, HttpServletRequest request) {
        try {
            String userId = jwtCommonService.getUserFromRequest(request);

            Product product = productRepository.findByParentAsin(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "ID", id));

            if (userId == null || !userId.equals(product.getUser().getUserId())) {
                throw new UnauthorizedException("You are not authorized to delete this product", "UNAUTHORIZED");
            }

            productRepository.deleteByParentAsin(id);

            return ResponseEntity.ok("Product with ID: " + id + " has been successfully deleted.");
        } catch (ResourceNotFoundException e) {
            log.error("Product not found with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorDetails(new Date(), e.getMessage(), null));
        } catch (UnauthorizedException e) {
            log.error("Unauthorized access while deleting product with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorDetails(new Date(), e.getMessage(), null));
        } catch (DataAccessException e) {
            log.error("Database access error while deleting product with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDetails(new Date(), "Error accessing the product database", null));
        } catch (Exception e) {
            log.error("Unexpected error while deleting product with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDetails(new Date(), "An unexpected error occurred while deleting product", null));
        }
    }

    @Override
    public ResponseEntity<ImageUploadResponse> addImageToProduct(String productId, MultipartFile hi_Res, MultipartFile large, MultipartFile thumb, String variant, HttpServletRequest request) {
        try {
            String userId = jwtCommonService.getUserFromRequest(request);

            Product product = productRepository.findByParentAsin(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "ID", productId));

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
            throw new ResourceNotFoundException("Product", "ID", productId);
        } catch (UnauthorizedException e) {
            log.error("Unauthorized access to product with ID: {}", productId, e);
            throw new UnauthorizedException("You are not authorized to upload images for this product", "UNAUTHORIZED");
        } catch (Exception e) {
            log.error("Unexpected error while uploading images for product with ID: {}", productId, e);
            throw new ApiRequestException("An unexpected error occurred while uploading images", e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ImageUploadResponse> updateImage(String productId, MultipartFile hiRes, MultipartFile large, MultipartFile thumb, String variant, int order, HttpServletRequest request) {
        try {
            if (order < 1) {
                throw new InvalidParameterException("Order parameter must be a positive integer starting from 1.");
            }

            String userId = jwtCommonService.getUserFromRequest(request);

            Product product = productRepository.findByParentAsin(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "ID", productId));

            if (!userId.equals(product.getUserId())) {
                throw new UnauthorizedException("You are not authorized to update images for this product", "UNAUTHORIZED");
            }

            List<Image> images = product.getImages();
            if (images == null || images.isEmpty() || order > images.size()) {
                throw new ResourceNotFoundException("Image", "order", String.valueOf(order));
            }

            ImageUploadResponse imageUploadResponse = imageService.addImage(productId, hiRes, large, thumb, variant);

            Image updatedImage = new Image(
                    imageUploadResponse.getThumb(),
                    imageUploadResponse.getLarge(),
                    imageUploadResponse.getVariant(),
                    imageUploadResponse.getHiRes()
            );
            images.set(order - 1, updatedImage);
            product.setImages(images);

            productRepository.save(product);

            return ResponseEntity.ok(imageUploadResponse);

        } catch (InvalidParameterException e) {
            log.error("Invalid parameter: {}", e.getMessage());
            throw new InvalidParameterException(e.getMessage());
        } catch (ResourceNotFoundException e) {
            log.error("Resource not found: {}", e.getMessage());
            throw new ResourceNotFoundException("Image", "order", String.valueOf(order));
        } catch (UnauthorizedException e) {
            log.error("Unauthorized access to product with ID: {}", productId, e);
            throw new UnauthorizedException("You are not authorized to update images for this product", "UNAUTHORIZED");
        } catch (Exception e) {
            log.error("Unexpected error while updating images for product with ID: {}", productId, e);
            throw new ApiRequestException("An unexpected error occurred while updating the image", e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> deleteImage(String productId, int order, HttpServletRequest request) {
        try {
            if (order < 1) {
                throw new InvalidParameterException("Order parameter must be a positive integer starting from 1.");
            }

            String userId = jwtCommonService.getUserFromRequest(request);

            Product product = productRepository.findByParentAsin(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "ID", productId));

            if (!userId.equals(product.getUserId())) {
                throw new UnauthorizedException("You are not authorized to delete images for this product", "UNAUTHORIZED");
            }

            List<Image> images = product.getImages();
            if (images == null || images.isEmpty() || order > images.size()) {
                throw new ResourceNotFoundException("Image", "order", String.valueOf(order));
            }

            images.remove(order - 1);
            product.setImages(images);

            productRepository.save(product);

            return ResponseEntity.ok("Image successfully deleted from product with ID: " + productId);

        } catch (InvalidParameterException e) {
            log.error("Invalid parameter: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorDetails(new Date(), e.getMessage(), null));
        } catch (ResourceNotFoundException e) {
            log.error("Resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorDetails(new Date(), e.getMessage(), null));
        } catch (UnauthorizedException e) {
            log.error("Unauthorized access to product with ID: {}", productId, e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorDetails(new Date(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Unexpected error while deleting images for product with ID: {}", productId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDetails(new Date(), "An unexpected error occurred while deleting the image", null));
        }
    }

    @Override
    public ResponseEntity<Page<ProductResponse>> getMyProducts(HttpServletRequest request, int page, int limit) {
        try{
            String userId = jwtCommonService.getUserFromRequest(request);

            Pageable pageable = PageRequest.of(page, limit);

            Page<Product> productPage = productRepository.findByUserId(userId, pageable);

            if (productPage.isEmpty()) {
                throw new ResourceNotFoundException("Product", "userId", userId);
            }

            Page<ProductResponse> productResponsePage = productPage.map(ProductMapper.INSTANCE::toProductResponse);

            return ResponseEntity.ok(productResponsePage);
        } catch (ResourceNotFoundException e) {
            log.error("No products found for user with ID: {}", e.getMessage());
            throw new ResourceNotFoundException("Product", "userId", e.getMessage());
        } catch (DataAccessException e) {
            log.error("Database access error while retrieving products for user with ID: {}", e.getMessage(), e);
            throw new DatabaseAccessException("Error accessing the product database while retrieving products for user with ID: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error while retrieving products for user with ID: {}", e.getMessage(), e);
            throw new ApiRequestException("An unexpected error occurred while retrieving products for user with ID: " + e.getMessage(), e.getMessage());
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
