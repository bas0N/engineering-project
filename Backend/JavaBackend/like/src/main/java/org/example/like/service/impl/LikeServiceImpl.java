package org.example.like.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.commondto.ProductEvent;
import org.example.exception.exceptions.*;
import org.example.jwtcommon.jwt.Utils;
import org.example.like.dto.ProductResponse;
import org.example.like.entity.Image;
import org.example.like.entity.Like;
import org.example.like.entity.Product;
import org.example.like.mapper.ImageMapper;
import org.example.like.mapper.LikeMapper;
import org.example.like.mapper.ProductMapper;
import org.example.like.repository.LikeRepository;
import org.example.like.repository.ProductRepository;
import org.example.like.response.LikeResponse;
import org.example.like.service.LikeService;
import org.example.like.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;
    private final Utils utils;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final LikeMapper likeMapper = LikeMapper.INSTANCE;
    private final ProductMapper productMapper = ProductMapper.INSTANCE;

    private final ImageMapper imageMapper = ImageMapper.INSTANCE;

    @Override
    public ResponseEntity<LikeResponse> addLike(String productUuid, HttpServletRequest request) {
        try {
            log.info("Adding like for product: {}", productUuid);

            String userUuid = utils.extractUserIdFromRequest(request);

            if (likeRepository.existsByUserIdAndProductId(userUuid, productUuid)) {
                throw new InvalidParameterException(
                        "Like already exists for user and product.",
                        "LIKE_ALREADY_EXISTS",
                        Map.of("userUuid", userUuid, "productUuid", productUuid)
                );
            }

            Product product = getOrFetchProduct(productUuid, userUuid);

            Like like = new Like(userUuid, product, new Date());
            likeRepository.saveAndFlush(like);

            LikeResponse likeResponse = likeMapper.toLikeResponse(like);
            return ResponseEntity.ok(likeResponse);

        } catch (InvalidTokenException e) {
            log.error("Invalid token while adding like for product: {}", productUuid, e);
            throw new UnauthorizedException(
                    "Invalid token",
                    "INVALID_TOKEN",
                    Map.of("error", e.getMessage())
            );
        } catch (LikeAlreadyExistsException e) {
            log.error("Like already exists for user and product: {}", productUuid, e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while adding like for product: {}", productUuid, e);
            throw new UnExpectedError(
                    "An unexpected error occurred while adding like",
                    e,
                    "ADD_LIKE_ERROR",
                    Map.of("productUuid", productUuid)
            );
        }
    }

    @Override
    public ResponseEntity<List<ProductResponse>> getMyLikedProducts(HttpServletRequest request) {
        try {
            String userUuid = utils.extractUserIdFromRequest(request);

            List<Product> productList = productRepository.findLikedProductsByUserId(userUuid);

            if (productList.isEmpty()) {
                throw new ResourceNotFoundException(
                        "Liked products not found for user",
                        "userId",
                        userUuid,
                        "LIKED_PRODUCTS_NOT_FOUND",
                        Map.of("userUuid", userUuid)
                );
            }

            List<ProductResponse> productResponseList = productMapper.toProductResponseList(productList);

            return ResponseEntity.ok(productResponseList);

        } catch (InvalidTokenException e) {
            log.error("Invalid token: {}", e.getMessage());
            throw new UnauthorizedException(
                    "Invalid token",
                    "INVALID_TOKEN",
                    Map.of("error", e.getMessage())
            );
        } catch (ResourceNotFoundException e) {
            log.error("No liked products found for user: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while fetching liked products", e);
            throw new UnExpectedError(
                    "An unexpected error occurred while fetching liked products",
                    e,
                    "GET_LIKED_PRODUCTS_ERROR",
                    Map.of("userUuid", utils.extractUserIdFromRequest(request))
            );
        }
    }

    @Override
    public ResponseEntity<Long> getNumberOfLikes(String productUuid) {
        try {
            Product product = productRepository.findByUuid(productUuid)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product",
                            "uuid",
                            productUuid,
                            "PRODUCT_NOT_FOUND",
                            Map.of("productUuid", productUuid)
                    ));

            Long likeCount = likeRepository.countByProductId(product.getId());

            return ResponseEntity.ok(likeCount);

        } catch (ResourceNotFoundException e) {
            log.error("Product not found with uuid: {}", productUuid, e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while retrieving like count for product: {}", productUuid, e);
            throw new UnExpectedError(
                    "An unexpected error occurred while retrieving like count",
                    e,
                    "GET_LIKE_COUNT_ERROR",
                    Map.of("productUuid", productUuid)
            );
        }
    }

    @Override
    public ResponseEntity<String> removeLike(String likeUuid, HttpServletRequest request) {
        try {
            String userUuid = utils.extractUserIdFromRequest(request);

            Like like = likeRepository.findByUuid(likeUuid)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Like",
                            "uuid",
                            likeUuid,
                            "LIKE_NOT_FOUND",
                            Map.of("likeUuid", likeUuid)
                    ));

            if (!Objects.equals(like.getUserId(), userUuid)) {
                throw new UnauthorizedException(
                        "User is not authorized to remove this like",
                        "UNAUTHORIZED",
                        Map.of("userUuid", userUuid, "likeOwnerId", like.getUserId())
                );
            }

            int deletedCount = likeRepository.deleteByUuid(likeUuid);
            if (deletedCount == 0) {
                throw new UnExpectedError(
                        "An error occurred while removing the like",
                        null,
                        "REMOVE_LIKE_ERROR",
                        Map.of("likeUuid", likeUuid)
                );
            }

            productRepository.findById(like.getProduct().getId())
                    .ifPresent(product -> {
                        if (!likeRepository.existsByProductId(product.getId())) {
                            productRepository.deleteById(product.getId());
                        }
                    });

            return ResponseEntity.ok("Like removed successfully");

        } catch (InvalidTokenException e) {
            log.error("Invalid token while removing like with UUID: {}", likeUuid, e);
            throw new UnauthorizedException(
                    "Invalid token",
                    "INVALID_TOKEN",
                    Map.of("error", e.getMessage())
            );
        } catch (ResourceNotFoundException | UnauthorizedException e) {
            log.error("Error occurred while removing like with UUID: {}", likeUuid, e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while removing like with UUID: {}", likeUuid, e);
            throw new UnExpectedError(
                    "An unexpected error occurred while removing the like",
                    e,
                    "REMOVE_LIKE_ERROR",
                    Map.of("likeUuid", likeUuid)
            );
        }
    }
    @Override
    public ResponseEntity<Boolean> isLiked(String productId, HttpServletRequest request) {
        try {
            String userId = utils.extractUserIdFromRequest(request);

            boolean isLiked = likeRepository.existsByUserIdAndProductId(userId, productId);

            return ResponseEntity.ok(isLiked);
        } catch (InvalidTokenException e) {
            log.error("Invalid token while checking like status for productId: {}", productId, e);
            throw new UnauthorizedException(
                    "Invalid token",
                    "INVALID_TOKEN",
                    Map.of("error", e.getMessage())
            );
        } catch (Exception e) {
            log.error("Unexpected error while checking like status for productId: {}", productId, e);
            throw new UnExpectedError(
                    "An unexpected error occurred while checking like status",
                    e,
                    "LIKE_STATUS_ERROR",
                    Map.of("productId", productId)
            );
        }
    }

    private Product getOrFetchProduct(String productUuid, String userUuid) {
        Optional<Product> optionalProduct = productRepository.findByUuid(productUuid);

        if (optionalProduct.isPresent()) {
            return optionalProduct.get();
        } else {
            try {
                ProductEvent productEvent = productService.getProduct(productUuid, userUuid);

                Product product = productMapper.toProduct(productEvent);
                List<Image> images = productEvent.getImages().stream()
                        .map(imageEvent -> imageMapper.toImage(imageEvent, product))
                        .toList();

                product.setImages(images);
                log.info("Saving fetched product: {}", product);
                return productRepository.saveAndFlush(product);

            } catch (Exception ex) {
                log.error("Failed to retrieve product details for productId: {}", productUuid, ex);
                throw new UnExpectedError(
                        "Could not retrieve product details",
                        ex,
                        "PRODUCT_FETCH_ERROR",
                        Map.of("productUuid", productUuid)
                );
            }
        }
    }
}