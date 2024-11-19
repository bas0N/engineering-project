package org.example.like.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.commondto.LikeEvent;
import org.example.commondto.ProductEvent;
import org.example.exception.exceptions.ApiRequestException;
import org.example.exception.exceptions.InvalidTokenException;
import org.example.exception.exceptions.LikeAlreadyExistsException;
import org.example.exception.exceptions.UnauthorizedException;
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
    private final ProductInfoProducer productInfoProducer;
    private final ProductInfoConsumer productInfoConsumer;
    private final ProductRepository productRepository;

    @Override
    public LikeResponse addLike(String productUuid, HttpServletRequest request) {
        try {
            log.info("adding like for product: {}", productUuid);
            String token = jwtCommonService.getTokenFromRequest(request);
            String userId = jwtCommonService.getCurrentUserId(token);

            if (likeRepository.existsByUserIdAndProductId(userId, productUuid)) {
                throw new LikeAlreadyExistsException("Like already exists for user and product.");
            }
            Optional<Product> optionalProduct = productRepository.findByUuid(productUuid);

            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();
                Like like = new Like(userId, product, new Date());
                likeRepository.save(like);
                return LikeMapper.INSTANCE.mapLikeToLikeResponse(like);
            } else {
                productInfoProducer.sendProductEvent(new LikeEvent(userId, productUuid));
                CompletableFuture<ProductEvent> productFuture = productInfoConsumer.getProductDetails(productUuid)
                        .orTimeout(30, TimeUnit.SECONDS)
                        .exceptionally(ex -> {
                            log.error("Failed to retrieve product details for productId: {}", productUuid, ex);
                            throw new ApiRequestException("Could not retrieve product details");
                        });

                ProductEvent productEvent = productFuture.join();
                Product product = ProductMapper.INSTANCE.toProduct(productEvent);

                List<Image> images = new ArrayList<>();
                productEvent.getImages().forEach(imageEvent -> {
                    images.add(ImageMapper.INSTANCE.toImage(imageEvent, product));
                });
                product.setImages(images);
                log.info("Saving product: {}", product);
                productRepository.saveAndFlush(product);
                Like like = likeRepository.findByUserIdAndProductId(userId, productUuid)
                        .orElseThrow(() -> new ApiRequestException("An error occurred while adding a like", "ADD_LIKE_ERROR"));
                return new LikeResponse(like.getUuid(), like.getProduct().getUuid(), like.getUserId(), like.getDateAdded());
            }
        } catch (InvalidTokenException e) {
            throw new UnauthorizedException("Invalid token: " + e.getMessage(), "INVALID_TOKEN");
        } catch (LikeAlreadyExistsException e) {
            throw new ApiRequestException("Like already exists: " + e.getMessage(), "LIKE_EXISTS");
        } catch (Exception e) {
            throw new ApiRequestException("An error occurred while adding a like: " + e.getMessage(), "ADD_LIKE_ERROR");
        }
    }

    @Override
    public List<ProductResponse> getMyLikedProducts(HttpServletRequest request) {
        String token = jwtCommonService.getTokenFromRequest(request);
        String userId = jwtCommonService.getCurrentUserId(token);

        List<Product> productList = productRepository.findLikedProductsByUserId(userId);
        return ProductMapper.INSTANCE.toProductResponseList(productList);
    }

    @Override
    public Long getNumberOfLikes(String productUuid) {
        Product product = productRepository.findByUuid(productUuid)
                .orElseThrow(() -> new ApiRequestException("Product not found", "PRODUCT_NOT_FOUND"));
        return likeRepository.countByProductId(product.getId());

    }

    @Override
    public void removeLike(String likeUuid, HttpServletRequest request) {
        Optional<Like> optionalLike = likeRepository.findByUuid(likeUuid);
        if (optionalLike.isEmpty()) {
            throw new ApiRequestException("Like does not exist", "LIKE_NOT_FOUND");
        }
        String token = jwtCommonService.getTokenFromRequest(request);
        String userId = jwtCommonService.getCurrentUserId(token);
        Like like = optionalLike.get();

        if (!Objects.equals(like.getUserId(), userId)) {
            throw new UnauthorizedException("User is not authorized to remove like", "UNAUTHORIZED");
        }
        if (likeRepository.deleteByUuid(likeUuid) == 0) {
            throw new ApiRequestException("An error occurred while removing like", "REMOVE_LIKE_ERROR");
        }
    }
}