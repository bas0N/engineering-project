package org.example.basket.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.TimeoutException;
import org.example.basket.dto.BasketItemDto;
import org.example.basket.dto.ListBasketItemDto;
import org.example.basket.dto.request.AddBasketItemRequest;
import org.example.basket.dto.request.DeleteItemRequest;
import org.example.basket.entity.Basket;
import org.example.basket.entity.BasketItems;
import org.example.basket.kafka.product.BasketConsumer;
import org.example.basket.kafka.product.BasketProducer;
import org.example.basket.repository.BasketItemRepository;
import org.example.basket.repository.BasketRepository;
import org.example.basket.service.BasketService;
import org.example.basket.service.ProductService;
import org.example.commondto.BasketItemEvent;
import org.example.commondto.BasketProductEvent;
import org.example.commondto.ListBasketItemEvent;
import org.example.exception.exceptions.*;
import org.example.jwtcommon.jwt.Utils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasketServiceImpl implements BasketService {
    private final BasketRepository basketRepository;
    private final BasketItemRepository basketItemRepository;
    private final BasketConsumer basketConsumer;
    private final BasketProducer basketProducer;
    private final Utils utils;
    private final ProductService productService;

    @Override
    public ResponseEntity<?> addProductToBasket(AddBasketItemRequest basketItemRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
            Basket basket = getOrCreateBasket(request);
            saveProductToBasket(basket, basketItemRequest);
            return ResponseEntity.ok("Product added to basket");
        } catch (InvalidTokenException e) {
            log.error("Invalid token while adding product to basket", e);
            throw new UnauthorizedException(
                    "Invalid token",
                    "INVALID_TOKEN",
                    Map.of("error", e.getMessage())
            );
        } catch (ResourceNotFoundException | InvalidParameterException e) {
            log.error("Error while adding product to basket: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while adding product to basket", e);
            throw new UnExpectedError(
                    "An unexpected error occurred while adding product to basket",
                    e,
                    "ADD_PRODUCT_TO_BASKET_ERROR",
                    Map.of("basketItemRequest", basketItemRequest)
            );
        }
    }

    private Basket getOrCreateBasket(HttpServletRequest request) {
        String userId = utils.extractUserIdFromRequest(request);
        return basketRepository.findByOwnerId(userId)
                .orElseGet(() -> {
                    Basket newBasket = new Basket();
                    newBasket.setUuid(UUID.randomUUID().toString());
                    newBasket.setOwnerId(userId);
                    return basketRepository.saveAndFlush(newBasket);
                });
    }

    private void saveProductToBasket(Basket basket, AddBasketItemRequest basketItemRequest) {
        try {
            BasketProductEvent product = getProductDetails(basketItemRequest.getProduct());
            if(product == null){
                throw new ResourceNotFoundException(
                        "Product",
                        "ID",
                        basketItemRequest.getProduct(),
                        "PRODUCT_NOT_FOUND",
                        Map.of("productId", basketItemRequest.getProduct())
                );
            }
            if(!product.getIsActive()){
                throw new ProductIsUnActive(product.getId());
            }
            basketItemRepository.findByBasketAndProduct(basket, product.getId()).ifPresentOrElse(existingItem -> {
                existingItem.setQuantity(existingItem.getQuantity() + basketItemRequest.getQuantity());
                basketItemRepository.save(existingItem);
            }, () -> {
                BasketItems newItem = new BasketItems();
                newItem.setBasket(basket);
                newItem.setUuid(UUID.randomUUID().toString());
                newItem.setQuantity(basketItemRequest.getQuantity());
                newItem.setProduct(product.getId());
                basketItemRepository.save(newItem);
            });
        } catch (ResourceNotFoundException e) {
            log.error("Product not found with ID: {}", basketItemRequest.getProduct(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while retrieving product details for product ID: {}", basketItemRequest.getProduct(), e);
            throw new UnExpectedError(
                    "An unexpected error occurred while retrieving product details",
                    e,
                    "PRODUCT_RETRIEVAL_ERROR",
                    Map.of("productId", basketItemRequest.getProduct())
            );
        }
    }

    @Override
    public ResponseEntity<?> deleteProductFromBasket(DeleteItemRequest deleteItemRequest, HttpServletRequest request) {
        try {
            Basket basket = getBasket(request);
            deleteItem(deleteItemRequest, basket);
            return ResponseEntity.ok("Product deleted from basket");
        } catch (InvalidTokenException e) {
            log.error("Invalid token while deleting product from basket", e);
            throw new UnauthorizedException(
                    "Invalid token",
                    "INVALID_TOKEN",
                    Map.of("error", e.getMessage())
            );
        } catch (ResourceNotFoundException e) {
            log.error("Error while deleting product from basket: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while deleting product from basket", e);
            throw new UnExpectedError(
                    "An unexpected error occurred while deleting product from basket",
                    e,
                    "DELETE_PRODUCT_FROM_BASKET_ERROR",
                    Map.of("deleteItemRequest", deleteItemRequest)
            );
        }
    }

    private Basket getBasket(HttpServletRequest request) {
        String userId = utils.extractUserIdFromRequest(request);
        return basketRepository.findByOwnerId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Basket",
                        "ownerId",
                        userId,
                        "BASKET_NOT_FOUND",
                        Map.of("ownerId", userId)
                ));
    }

    private void deleteItem(DeleteItemRequest deleteItemRequest, Basket basket) {
        String basketItemUuid = deleteItemRequest.getBasketItemUuid();
        Long quantityToRemove = deleteItemRequest.getQuantity();
        basketItemRepository.findByUuidAndBasket(basketItemUuid, basket)
                .ifPresentOrElse(basketItem -> {
                    if (quantityToRemove != null && quantityToRemove < basketItem.getQuantity()) {
                        basketItem.setQuantity(basketItem.getQuantity() - quantityToRemove);
                        basketItemRepository.save(basketItem);
                    } else {
                        basketItemRepository.delete(basketItem);
                    }
                }, () -> {
                    throw new ResourceNotFoundException(
                            "Basket item",
                            "uuid",
                            basketItemUuid,
                            "BASKET_ITEM_NOT_FOUND",
                            Map.of("basketItemUuid", basketItemUuid)
                    );
                });

        Long sum = basketItemRepository.sumBasketItems(basket.getId());
        if (sum == null || sum == 0) {
            basketRepository.delete(basket);
        }
    }

    @Override
    public ResponseEntity<?> getItems(HttpServletRequest request) {
        try {
            Basket basket = getBasket(request);
            ListBasketItemDto listBasketItemDTO = buildBasketItemDto(basket);
            return ResponseEntity.ok(listBasketItemDTO);
        } catch (InvalidTokenException e) {
            log.error("Invalid token while retrieving basket items", e);
            throw new UnauthorizedException(
                    "Invalid token",
                    "INVALID_TOKEN",
                    Map.of("error", e.getMessage())
            );
        } catch (ResourceNotFoundException e) {
            log.error("Error while retrieving basket items: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while retrieving basket items", e);
            throw new UnExpectedError(
                    "An unexpected error occurred while retrieving basket items",
                    e,
                    "GET_BASKET_ITEMS_ERROR",
                    Map.of("userId", utils.extractUserIdFromRequest(request))
            );
        }
    }

    private ListBasketItemDto buildBasketItemDto(Basket basket) {
        ListBasketItemDto listBasketItemDTO = new ListBasketItemDto();
        listBasketItemDTO.setBasketProducts(new ArrayList<>());

        basketItemRepository.findBasketItemsByBasket(basket).forEach(item -> {
            BasketProductEvent product = getProductDetails(item.getProduct());

            if (product != null) {
                listBasketItemDTO.getBasketProducts().add(new BasketItemDto(
                        product.getId(),
                        product.getName(),
                        item.getQuantity(),
                        product.getImageUrls().getFirst(),
                        product.getPrice(),
                        product.getPrice() * item.getQuantity(),
                        product.getIsActive()
                ));
                listBasketItemDTO.setSummaryPrice(
                        listBasketItemDTO.getSummaryPrice() + (item.getQuantity() * product.getPrice())
                );
                listBasketItemDTO.setSummaryQuantity(listBasketItemDTO.getSummaryQuantity() + item.getQuantity());
            } else {
                throw new ResourceNotFoundException(
                        "Product",
                        "ID",
                        item.getProduct(),
                        "PRODUCT_NOT_FOUND",
                        Map.of("productId", item.getProduct())
                );
            }
        });

        return listBasketItemDTO;
    }

    @Override
    public void removeBasketById(String basketId) {
        try {
            Basket basket = basketRepository.findByUuid(basketId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Basket",
                            "uuid",
                            basketId,
                            "BASKET_NOT_FOUND",
                            Map.of("basketId", basketId)
                    ));
            int itemsDeleted = basketItemRepository.deleteAllByBasket(basket);
            if (itemsDeleted <= 0) {
                throw new UnExpectedError(
                        "Removing basket items failed",
                        null,
                        "BASKET_ITEMS_REMOVE_FAILED",
                        Map.of("basketId", basketId)
                );
            }
            int basketDelete = basketRepository.deleteByUuid(basketId);
            if (basketDelete <= 0) {
                throw new UnExpectedError(
                        "Removing basket failed",
                        null,
                        "BASKET_REMOVE_FAILED",
                        Map.of("basketId", basketId)
                );
            }
        } catch (ResourceNotFoundException e) {
            log.error("Basket not found with ID: {}", basketId, e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while removing basket with ID: {}", basketId, e);
            throw new UnExpectedError(
                    "An unexpected error occurred while removing basket",
                    e,
                    "REMOVE_BASKET_ERROR",
                    Map.of("basketId", basketId)
            );
        }
    }

    @Override
    public ListBasketItemEvent getBasketItems(String basketId) {
        try {
            Basket basket = basketRepository.findByUuid(basketId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Basket",
                            "uuid",
                            basketId,
                            "BASKET_NOT_FOUND",
                            Map.of("basketId", basketId)
                    ));
            ListBasketItemEvent listBasketItemEvent = buildBasketItemEvent(basket);
            log.info("Basket items retrieved successfully for basket ID: {}", basketId);
            return listBasketItemEvent;
        } catch (ResourceNotFoundException e) {
            log.error("Basket not found with ID: {}", basketId, e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while retrieving basket items for basket ID: {}", basketId, e);
            throw new UnExpectedError(
                    "An unexpected error occurred while retrieving basket items",
                    e,
                    "GET_BASKET_ITEMS_ERROR",
                    Map.of("basketId", basketId)
            );
        }
    }

    private ListBasketItemEvent buildBasketItemEvent(Basket basket) {
        ListBasketItemEvent listBasketItemEvent = new ListBasketItemEvent();
        List<BasketItemEvent> basketProducts = new ArrayList<>();
        listBasketItemEvent.setBasketId(basket.getUuid());
        AtomicReference<Double> summaryPrice = new AtomicReference<>(0.0);

        basketItemRepository.findBasketItemsByBasket(basket).forEach(item -> {
            BasketProductEvent product = getProductDetails(item.getProduct());
            if (product != null && product.getIsActive()) {
                basketProducts.add(new BasketItemEvent(
                        product.getId(),
                        product.getName(),
                        item.getQuantity(),
                        product.getImageUrls().getFirst(),
                        product.getPrice(),
                        product.getPrice() * item.getQuantity()
                ));
                summaryPrice.updateAndGet(v -> v + product.getPrice() * item.getQuantity());
            } else {
                throw new ResourceNotFoundException(
                        "Product",
                        "ID",
                        item.getProduct(),
                        "PRODUCT_NOT_FOUND",
                        Map.of("productId", item.getProduct())
                );
            }
        });

        listBasketItemEvent.setBasketProducts(basketProducts);
        listBasketItemEvent.setSummaryPrice(summaryPrice.get());
        return listBasketItemEvent;
    }

    private BasketProductEvent getProductDetails(String productId) {
        try {
            BasketProductEvent product = productService.getProductById(productId);
            log.info("Product details retrieved successfully for product ID: {}", productId);
            return product;

        } catch (TimeoutException e) {
            log.error("Timeout while retrieving product details for product ID: {}", productId, e);
            throw new DatabaseAccessException(
                    "Timeout while retrieving product details",
                    e,
                    "PRODUCT_SERVICE_TIMEOUT",
                    Map.of("productId", productId)
            );
        } catch (Exception e) {
            log.error("Unexpected error while retrieving product details for product ID: {}", productId, e);
            throw new UnExpectedError(
                    "An unexpected error occurred while retrieving product details",
                    e,
                    "PRODUCT_RETRIEVAL_ERROR",
                    Map.of("productId", productId)
            );
        }
    }
}
