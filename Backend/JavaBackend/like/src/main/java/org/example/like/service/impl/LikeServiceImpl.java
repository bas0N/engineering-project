package org.example.like.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.example.commondto.LikeEvent;
import org.example.exception.exceptions.ApiRequestException;
import org.example.exception.exceptions.InvalidTokenException;
import org.example.exception.exceptions.LikeAlreadyExistsException;
import org.example.exception.exceptions.UnauthorizedException;
import org.example.jwtcommon.jwt.JwtCommonService;
import org.example.like.entity.Like;
import org.example.like.entity.Product;
import org.example.like.mapper.LikeMapper;
import org.example.like.repository.LikeRepository;
import org.example.like.repository.ProductRepository;
import org.example.like.response.LikeResponse;
import org.example.like.service.LikeService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;
    private final JwtCommonService jwtCommonService;

    private final KafkaTemplate<String, LikeEvent> kafkaTemplate;

    private final ProductRepository productRepository;

    public LikeServiceImpl(LikeRepository likeRepository, JwtCommonService jwtCommonService, KafkaTemplate<String, LikeEvent> kafkaTemplate, ProductRepository productRepository) {
        this.likeRepository = likeRepository;
        this.jwtCommonService = jwtCommonService;
        this.kafkaTemplate = kafkaTemplate;
        this.productRepository = productRepository;
    }


    public LikeResponse addLike(String productUuid, HttpServletRequest request) {
        try {
            // Pobranie tokena i ID użytkownika
            String token = jwtCommonService.getTokenFromRequest(request);
            String userId = jwtCommonService.getCurrentUserId(token);

            // Sprawdzenie, czy Like już istnieje
            if (likeRepository.existsByUserIdAndProductId(userId, productUuid)) {
                throw new LikeAlreadyExistsException("Like already exists for user and product.");
            }

            // Znalezienie produktu na podstawie UUID
            Optional<Product> optionalProduct = productRepository.findByUuid(productUuid);

            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();
                Like like = new Like(userId, product, new Date());
                likeRepository.save(like);
                return LikeMapper.INSTANCE.mapLikeToLikeResponse(like);
            } else {
                // Produkt nie istnieje, wysyłamy event do Kafki
                LikeEvent likeEvent = new LikeEvent(userId, productUuid);
                kafkaTemplate.send("like-events", likeEvent);
                return new LikeResponse(userId, productUuid, "Like event sent for non-existing product.");
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
    public List<Product> getMyLikedProducts(HttpServletRequest request) {
        String token = jwtCommonService.getTokenFromRequest(request);
        String userId = jwtCommonService.getCurrentUserId(token);

        return productRepository.findLikedProductsByUserId(userId);
    }

    @Override
    public Object getNumberOfLikes(String productUuid) {
        Product product = productRepository.findByUuid(productUuid)
                .orElseThrow(() -> new ApiRequestException("Product not found", "PRODUCT_NOT_FOUND"));
        return likeRepository.countByProductId(product.getId());
    }

    @Override
    public void removeLike(String likeUuid) {
        if (!likeRepository.existsByUuid(likeUuid)) {
            throw new ApiRequestException("Like does not exist", "LIKE_NOT_FOUND");
        }
        likeRepository.deleteByUuid(likeUuid);
    }
}
