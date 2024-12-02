package org.example.like.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.commondto.LikeEvent;
import org.example.commondto.ProductEvent;
import org.example.exception.exceptions.*;
import org.example.jwtcommon.jwt.JwtCommonService;
import org.example.like.dto.ProductResponse;
import org.example.like.entity.Image;
import org.example.like.entity.Like;
import org.example.like.entity.Product;
import org.example.like.kafka.ProductInfoConsumer;
import org.example.like.kafka.ProductInfoProducer;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;
    private final JwtCommonService jwtCommonService;
    private final ProductRepository productRepository;
    private final ProductService productService;

    @Override
    public ResponseEntity<LikeResponse> addLike(String productUuid, HttpServletRequest request) {
        try {
            log.info("Adding like for product: {}", productUuid);

            String userUuid = jwtCommonService.getUserFromRequest(request);

            if (likeRepository.existsByUserIdAndProductId(userUuid, productUuid)) {
                throw new LikeAlreadyExistsException("Like already exists for user and product.");
            }

            Product product = getOrFetchProduct(productUuid, userUuid);

            Like like = new Like(userUuid, product, new Date());
            likeRepository.save(like);

            LikeResponse likeResponse = LikeMapper.INSTANCE.toLikeResponse(like);
            return ResponseEntity.ok(likeResponse);

        } catch (InvalidTokenException e) {
            log.error("Invalid token while adding like for product: {}", productUuid, e);
            throw new UnauthorizedException("Invalid token: " + e.getMessage(), "INVALID_TOKEN");
        } catch (LikeAlreadyExistsException e) {
            log.error("Like already exists for user and product: {}", productUuid, e);
            throw new ApiRequestException("Like already exists: " + e.getMessage(), "LIKE_EXISTS");
        } catch (Exception e) {
            log.error("Unexpected error while adding like for product: {}", productUuid, e);
            throw new UnExpectedError("An unexpected error occurred while adding like", e);
        }
    }

    @Override
    public ResponseEntity<List<ProductResponse>> getMyLikedProducts(HttpServletRequest request) {
        try {
            String userUuid = jwtCommonService.getUserFromRequest(request);

            List<Product> productList = productRepository.findLikedProductsByUserId(userUuid);

            if (productList.isEmpty()) {
                throw new ResourceNotFoundException("Liked products not found for user", "userId", userUuid);
            }

            List<ProductResponse> productResponseList = ProductMapper.INSTANCE.toProductResponseList(productList);

            return ResponseEntity.ok(productResponseList);

        } catch (InvalidTokenException e) {
            log.error("Invalid token: {}", e.getMessage());
            throw new UnauthorizedException("Invalid token: " + e.getMessage(), "INVALID_TOKEN");
        } catch (ResourceNotFoundException e) {
            log.error("No liked products found for user: {}", e.getMessage());
            throw new ResourceNotFoundException("No liked products found for user", "USER_ID", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while fetching liked products", e);
            throw new UnExpectedError("An unexpected error occurred while fetching liked products", e);
        }
    }

    @Override
    public ResponseEntity<Long> getNumberOfLikes(String productUuid) {
        try {
            Product product = productRepository.findByUuid(productUuid)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "uuid", productUuid));

            Long likeCount = likeRepository.countByProductId(product.getId());

            return ResponseEntity.ok(likeCount);

        } catch (ResourceNotFoundException e) {
            log.error("Product not found with uuid: {}", productUuid, e);
            throw new ResourceNotFoundException("Product not found", "uuid", productUuid);
        } catch (Exception e) {
            log.error("Unexpected error while retrieving like count for product: {}", productUuid, e);
            throw new UnExpectedError("An unexpected error occurred while retrieving like count", e);
        }
    }

    @Override
    public ResponseEntity<?> removeLike(String likeUuid, HttpServletRequest request) {
        try {
            Like like = likeRepository.findByUuid(likeUuid)
                    .orElseThrow(() -> new ApiRequestException("Like does not exist", "LIKE_NOT_FOUND"));

            String userUuid = jwtCommonService.getUserFromRequest(request);

            if (!Objects.equals(like.getUserId(), userUuid)) {
                throw new UnauthorizedException("User is not authorized to remove this like", "UNAUTHORIZED");
            }

            int deletedCount = likeRepository.deleteByUuid(likeUuid);
            if (deletedCount == 0) {
                throw new ApiRequestException("An error occurred while removing the like", "REMOVE_LIKE_ERROR");
            }

            productRepository.findById(like.getProduct().getId())
                    .ifPresent(product -> {
                        if (!likeRepository.existsByProductId(product.getId())) {
                            productRepository.deleteById(product.getId());
                        }
                    });

            return ResponseEntity.ok().build();
        } catch (UnauthorizedException e) {
            log.error("Unauthorized access while removing like with UUID: {}", likeUuid, e);
            throw e;
        } catch (ApiRequestException e) {
            log.error("Error occurred while removing like with UUID: {}", likeUuid, e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while removing like with UUID: {}", likeUuid, e);
            throw new ApiRequestException("An unexpected error occurred while removing the like", e, "REMOVE_LIKE_ERROR");
        }
    }
    @Override
    public ResponseEntity<Boolean> isLiked(String productId, HttpServletRequest request) {
        try {
            String userId = jwtCommonService.getUserFromRequest(request);

            boolean isLiked = likeRepository.existsByUserIdAndProductId(userId, productId);

            return ResponseEntity.ok(isLiked);
        } catch (InvalidTokenException e) {
            log.error("Invalid token while checking like status for productId: {}", productId, e);
            throw new UnauthorizedException("Invalid token: " + e.getMessage(), "INVALID_TOKEN");
        } catch (Exception e) {
            log.error("Unexpected error while checking like status for productId: {}", productId, e);
            throw new ApiRequestException("An unexpected error occurred while checking like status", e, "LIKE_STATUS_ERROR");
        }
    }


    private Product getOrFetchProduct(String productUuid, String userUuid) {
        Optional<Product> optionalProduct = productRepository.findByUuid(productUuid);

        if (optionalProduct.isPresent()) {
            return optionalProduct.get();
        } else {
            try {
                ProductEvent productEvent = productService.getProduct(productUuid, userUuid);

                Product product = ProductMapper.INSTANCE.toProduct(productEvent);
                List<Image> images = productEvent.getImages().stream()
                        .map(imageEvent -> ImageMapper.INSTANCE.toImage(imageEvent, product))
                        .toList();

                product.setImages(images);
                log.info("Saving fetched product: {}", product);
                return productRepository.saveAndFlush(product);

            } catch (Exception ex) {
                log.error("Failed to retrieve product details for productId: {}", productUuid, ex);
                throw new ApiRequestException("Could not retrieve product details", ex, "PRODUCT_FETCH_ERROR");
            }
        }
    }
}