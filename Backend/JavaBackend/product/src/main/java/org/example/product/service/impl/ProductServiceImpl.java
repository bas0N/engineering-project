package org.example.product.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.commondto.UserDetailInfoEvent;
import org.example.exception.exceptions.*;
import org.example.jwtcommon.jwt.Utils;
import org.example.product.dto.Request.AddProductRequest;
import org.example.product.dto.Request.UpdateProductRequest;
import org.example.product.dto.Response.ImageUploadResponse;
import org.example.product.dto.Response.ProductDetailResponse;
import org.example.product.dto.Response.ProductResponse;
import org.example.product.entity.Image;
import org.example.product.entity.Product;
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
import org.springframework.data.mongodb.core.query.Update;
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
    private final Utils utils;
    private final ImageService imageService;
    private final MongoTemplate mongoTemplate;
    private final UserService userService;
    private final ProductMapper productMapper = ProductMapper.INSTANCE;
    private final UserMapper userMapper = UserMapper.INSTANCE;

    public ResponseEntity<Page<ProductResponse>> getProducts(int page, int limit, String sort, String mainCategory, String title, Double minPrice, Double maxPrice, Double minRating, Double maxRating, List<String> categories, String store) {
        try {
            validateRequestParameters(page, limit, minPrice, maxPrice, minRating, maxRating);
            Query query = new Query();

            query.addCriteria(Criteria.where("isActive").is(true));
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
                throw new ResourceNotFoundException(
                        "Product",
                        "query",
                        query.toString(),
                        "PRODUCTS_NOT_FOUND",
                        Map.of("query", query.toString())
                );
            }

            List<ProductResponse> productResponses = products.stream()
                    .map(productMapper::toProductResponse)
                    .toList();

            return ResponseEntity.ok(new PageImpl<>(productResponses, pageable, products.size()));

        } catch (DataAccessException e) {
            log.error("Database access error while retrieving products", e);
            throw new DatabaseAccessException(
                    "Error accessing the product database",
                    e,
                    "DATABASE_ACCESS_ERROR",
                    Map.of("operation", "getProducts")
            );
        } catch (Exception e) {
            log.error("Unexpected error while retrieving products", e);
            throw new UnExpectedError(
                    "An unexpected error occurred while retrieving products",
                    e,
                    "GET_PRODUCTS_ERROR",
                    Map.of("error", e.getMessage())
            );
        }
    }

    public ResponseEntity<ProductDetailResponse> getProductById(String parentAsin, HttpServletRequest request) {
        try {
            Product product = productRepository.findByParentAsin(parentAsin)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product",
                            "parentAsin",
                            parentAsin,
                            "PRODUCT_NOT_FOUND",
                            Map.of("parentAsin", parentAsin)
                    ));

            String userId = utils.extractUserIdFromRequest(request);

            UserDetailInfoEvent userInfo = userService.getUserDetailInfo(userId);
            if(!userInfo.isActive()){
                throw new UserIsUnActiveException("User is not active");
            }

            ProductDetailResponse productResponse = productMapper.toProductDetailResponse(product, userMapper.toUser(userInfo));

            return ResponseEntity.ok(productResponse);
        } catch (ResourceNotFoundException e) {
            log.error("Product not found with parentAsin: {}", parentAsin, e);
            throw e;
        } catch (DataAccessException e) {
            log.error("Database access error while retrieving product with parentAsin: {}", parentAsin, e);
            throw new DatabaseAccessException(
                    "Error accessing the product database",
                    e,
                    "DATABASE_ACCESS_ERROR",
                    Map.of("operation", "getProductById", "parentAsin", parentAsin)
            );
        } catch (Exception e) {
            log.error("Unexpected error while retrieving product with parentAsin: {}", parentAsin, e);
            throw new UnExpectedError(
                    "An unexpected error occurred while retrieving product",
                    e,
                    "GET_PRODUCT_BY_ID_ERROR",
                    Map.of("parentAsin", parentAsin)
            );
        }
    }

    @Override
    public ResponseEntity<ProductDetailResponse> createProduct(AddProductRequest addProductRequest, HttpServletRequest request) {
        try {
            String userId = utils.extractUserIdFromRequest(request);

            UserDetailInfoEvent userInfo = userService.getUserDetailInfo(userId);

            if (userInfo == null) {
                throw new ResourceNotFoundException(
                        "User",
                        "ID",
                        userId,
                        "USER_NOT_FOUND",
                        Map.of("userId", userId)
                );
            }

            if (userInfo.getFirstName() == null || userInfo.getLastName() == null || userInfo.getEmail() == null) {
                List<String> missingDetails = new ArrayList<>();
                if (userInfo.getFirstName() == null) missingDetails.add("First Name");
                if (userInfo.getLastName() == null) missingDetails.add("Last Name");
                if (userInfo.getEmail() == null) missingDetails.add("Email");

                throw new MissingUserDetailsException(
                        "User must complete profile information. Missing details: " + String.join(", ", missingDetails),
                        "MISSING_USER_DETAILS",
                        Map.of("missingDetails", missingDetails)
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
                    true
            );

            Product savedProduct = productRepository.save(product);

            ProductDetailResponse productResponse = productMapper.toProductDetailResponse(savedProduct, userMapper.toUser(userInfo));
            return ResponseEntity.ok(productResponse);

        } catch (MissingUserDetailsException e) {
            log.error("Missing user details: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            log.error("Database access error while creating product", e);
            throw new DatabaseAccessException(
                    "Error accessing the product database while creating product",
                    e,
                    "DATABASE_ACCESS_ERROR",
                    Map.of("operation", "createProduct")
            );
        } catch (Exception e) {
            log.error("Unexpected error while creating product", e);
            throw new UnExpectedError(
                    "An unexpected error occurred while creating product",
                    e,
                    "CREATE_PRODUCT_ERROR",
                    Map.of("error", e.getMessage())
            );
        }
    }

    @Override
    public ResponseEntity<ProductDetailResponse> updateProduct(String id, UpdateProductRequest updateProductRequest, HttpServletRequest request) {
        try {
            Product existingProduct = productRepository.findByParentAsin(id)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product",
                            "ID",
                            id,
                            "PRODUCT_NOT_FOUND",
                            Map.of("productId", id)
                    ));

            String userId = utils.extractUserIdFromRequest(request);

            if (userId != null && !userId.equals(existingProduct.getUserId())) {
                throw new UnauthorizedException(
                        "You are not authorized to update this product",
                        "UNAUTHORIZED",
                        Map.of("userId", userId, "productOwnerId", existingProduct.getUserId())
                );
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
            UserDetailInfoEvent userInfo = userService.getUserDetailInfo(userId);

            ProductDetailResponse productResponse =productMapper.toProductDetailResponse(savedProduct, userMapper.toUser(userInfo));
            return ResponseEntity.ok(productResponse);
        } catch (ResourceNotFoundException | UnauthorizedException e) {
            log.error("Product not found: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            log.error("Database access error while updating product", e);
            throw new DatabaseAccessException(
                    "Error accessing the product database while updating product",
                    e,
                    "DATABASE_ACCESS_ERROR",
                    Map.of("operation", "updateProduct", "productId", id)
            );
        } catch (Exception e) {
            log.error("Unexpected error while updating product", e);
            throw new UnExpectedError(
                    "An unexpected error occurred while updating product",
                    e,
                    "UPDATE_PRODUCT_ERROR",
                    Map.of("productId", id)
            );
        }
    }

    @Override
    public ResponseEntity<String> deleteProduct(String id, HttpServletRequest request) {
        try {
            String userId = utils.extractUserIdFromRequest(request);

            Product product = productRepository.findByParentAsin(id)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product",
                            "ID",
                            id,
                            "PRODUCT_NOT_FOUND",
                            Map.of("productId", id)
                    ));


            if (userId == null || !userId.equals(product.getUserId())) {
                throw new UnauthorizedException(
                        "You are not authorized to delete this product",
                        "UNAUTHORIZED",
                        Map.of("userId", userId, "productOwnerId", product.getUserId())
                );
            }

            Query query = new Query(Criteria.where("parent_asin").is(id));
            Update update = new Update().set("isActive", false);
            mongoTemplate.updateMulti(query, update, Product.class);

            return ResponseEntity.ok("Product with ID: " + id + " has been successfully deleted.");

        } catch (ResourceNotFoundException | UnauthorizedException e) {
            log.error("Error deleting product: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            log.error("Database access error while deleting product with ID: {}", id, e);
            throw new DatabaseAccessException(
                    "Error accessing the product database while deleting product",
                    e,
                    "DATABASE_ACCESS_ERROR",
                    Map.of("operation", "deleteProduct", "productId", id)
            );
        } catch (Exception e) {
            log.error("Unexpected error while deleting product with ID: {}", id, e);
            throw new UnExpectedError(
                    "An unexpected error occurred while deleting product",
                    e,
                    "DELETE_PRODUCT_ERROR",
                    Map.of("productId", id)
            );
        }
    }

    @Override
    public ResponseEntity<ImageUploadResponse> addImageToProduct(String productId, MultipartFile hi_Res, MultipartFile large, MultipartFile thumb, String variant, HttpServletRequest request) {
        try {
            String userId = utils.extractUserIdFromRequest(request);

            Product product = productRepository.findByParentAsin(productId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product",
                            "ID",
                            productId,
                            "PRODUCT_NOT_FOUND",
                            Map.of("productId", productId)
                    ));

            if (!userId.equals(product.getUserId())) {
                throw new UnauthorizedException(
                        "You are not authorized to upload images for this product",
                        "UNAUTHORIZED",
                        Map.of("userId", userId, "productOwnerId", product.getUserId())
                );
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

        } catch (ResourceNotFoundException | UnauthorizedException e) {
            log.error("Error adding image to product: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while uploading images for product with ID: {}", productId, e);
            throw new UnExpectedError(
                    "An unexpected error occurred while uploading images",
                    e,
                    "ADD_IMAGE_ERROR",
                    Map.of("productId", productId)
            );
        }
    }

    @Override
    public ResponseEntity<ImageUploadResponse> updateImage(String productId, MultipartFile hiRes, MultipartFile large, MultipartFile thumb, String variant, int order, HttpServletRequest request) {
        try {
            if (order < 1) {
                throw new InvalidParameterException(
                        "Order parameter must be a positive integer starting from 1.",
                        "INVALID_ORDER_PARAMETER",
                        Map.of("order", order)
                );
            }

            String userId = utils.extractUserIdFromRequest(request);

            Product product = productRepository.findByParentAsin(productId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product",
                            "ID",
                            productId,
                            "PRODUCT_NOT_FOUND",
                            Map.of("productId", productId)
                    ));

            if (!userId.equals(product.getUserId())) {
                throw new UnauthorizedException(
                        "You are not authorized to update images for this product",
                        "UNAUTHORIZED",
                        Map.of("userId", userId, "productOwnerId", product.getUserId())
                );
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

        } catch (InvalidParameterException | ResourceNotFoundException | UnauthorizedException e) {
            log.error("Error updating image: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while updating images for product with ID: {}", productId, e);
            throw new UnExpectedError(
                    "An unexpected error occurred while updating the image",
                    e,
                    "UPDATE_IMAGE_ERROR",
                    Map.of("productId", productId, "order", order)
            );
        }
    }

    @Override
    public ResponseEntity<String> deleteImage(String productId, int order, HttpServletRequest request) {
        try {
            if (order < 1) {
                throw new InvalidParameterException(
                        "Order parameter must be a positive integer starting from 1.",
                        "INVALID_ORDER_PARAMETER",
                        Map.of("order", order)
                );
            }

            String userId = utils.extractUserIdFromRequest(request);

            Product product = productRepository.findByParentAsin(productId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product",
                            "ID",
                            productId,
                            "PRODUCT_NOT_FOUND",
                            Map.of("productId", productId)
                    ));

            if (!userId.equals(product.getUserId())) {
                throw new UnauthorizedException(
                        "You are not authorized to delete images for this product",
                        "UNAUTHORIZED",
                        Map.of("userId", userId, "productOwnerId", product.getUserId())
                );
            }

            List<Image> images = product.getImages();
            if (images == null || images.isEmpty() || order > images.size()) {
                throw new ResourceNotFoundException(
                        "Image",
                        "order",
                        String.valueOf(order),
                        "IMAGE_NOT_FOUND",
                        Map.of("order", order)
                );
            }

            images.remove(order - 1);
            product.setImages(images);

            productRepository.save(product);

            return ResponseEntity.ok("Image successfully deleted from product with ID: " + productId);

        } catch (InvalidParameterException | ResourceNotFoundException | UnauthorizedException e) {
            log.error("Error deleting image: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while deleting images for product with ID: {}", productId, e);
            throw new UnExpectedError(
                    "An unexpected error occurred while deleting the image",
                    e,
                    "DELETE_IMAGE_ERROR",
                    Map.of("productId", productId, "order", order)
            );
        }
    }

    @Override
    public ResponseEntity<Page<ProductResponse>> getMyProducts(HttpServletRequest request, int page, int limit) {
        try {
            String userId = utils.extractUserIdFromRequest(request);

            if (page < 0 || limit <= 0) {
                throw new InvalidParameterException(
                        "Page index must be >= 0 and limit must be > 0",
                        "INVALID_PAGINATION_PARAMETERS",
                        Map.of("page", page, "limit", limit)
                );
            }

            Pageable pageable = PageRequest.of(page, limit);

            Page<Product> productPage = productRepository.findByUserId(userId, pageable);

            if (productPage.isEmpty()) {
                throw new ResourceNotFoundException(
                        "Product",
                        "userId",
                        userId,
                        "PRODUCTS_NOT_FOUND",
                        Map.of("userId", userId)
                );
            }

            Page<ProductResponse> productResponsePage = productPage.map(productMapper::toProductResponse);

            return ResponseEntity.ok(productResponsePage);
        } catch (InvalidParameterException | ResourceNotFoundException e) {
            log.error("Error retrieving products: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            log.error("Database access error while retrieving products for user with ID: {}", e.getMessage(), e);
            throw new DatabaseAccessException(
                    "Error accessing the product database",
                    e,
                    "DATABASE_ACCESS_ERROR",
                    Map.of("operation", "getMyProducts", "userId", e.getMessage())
            );
        } catch (Exception e) {
            log.error("Unexpected error while retrieving products for user with ID: {}", e.getMessage(), e);
            throw new UnExpectedError(
                    "An unexpected error occurred while retrieving products",
                    e,
                    "GET_MY_PRODUCTS_ERROR",
                    Map.of("userId", e.getMessage())
            );
        }
    }

    private String generateUuid() {
        return UUID.randomUUID().toString();
    }

    private void validateRequestParameters(int page, int limit, Double minPrice, Double maxPrice, Double minRating, Double maxRating) {
        if (page < 1 || limit < 1) {
            throw new InvalidParameterException(
                    "Page and limit parameters must be positive.",
                    "INVALID_PAGINATION_PARAMETERS",
                    Map.of("page", page, "limit", limit)
            );
        }
        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw new InvalidParameterException(
                    "Minimum price cannot be greater than maximum price.",
                    "INVALID_PRICE_RANGE",
                    Map.of("minPrice", minPrice, "maxPrice", maxPrice)
            );
        }
        if (minRating != null && maxRating != null && minRating > maxRating) {
            throw new InvalidParameterException(
                    "Minimum rating cannot be greater than maximum rating.",
                    "INVALID_RATING_RANGE",
                    Map.of("minRating", minRating, "maxRating", maxRating)
            );
        }
    }

}
