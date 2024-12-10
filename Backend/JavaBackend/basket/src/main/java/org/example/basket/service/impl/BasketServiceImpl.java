package org.example.basket.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.basket.dto.BasketItemDto;
import org.example.basket.dto.ListBasketItemDto;
import org.example.basket.dto.request.AddBasketItemRequest;
import org.example.basket.dto.request.DeleteItemRequest;
import org.example.basket.dto.response.BasketItemResponse;
import org.example.basket.entity.Basket;
import org.example.basket.entity.BasketItems;
import org.example.basket.repository.BasketItemRepository;
import org.example.basket.repository.BasketRepository;
import org.example.basket.service.BasketService;
import org.example.basket.service.ProductService;
import org.example.commondto.BasketItemEvent;
import org.example.commondto.BasketProductEvent;
import org.example.commondto.ListBasketItemEvent;
import org.example.commonutils.Utils;
import org.example.exception.exceptions.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasketServiceImpl implements BasketService {
    private final BasketRepository basketRepository;
    private final BasketItemRepository basketItemRepository;
    private final Utils utils;
    private final ProductService productService;

    @Override
    public ResponseEntity<BasketItemResponse> addProductToBasket(AddBasketItemRequest basketItemRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
            Basket basket = getOrCreateBasket(request);
            BasketItemResponse basketItemResponse = saveProductToBasket(basket, basketItemRequest);
            return ResponseEntity.ok(basketItemResponse);
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

    private BasketItemResponse saveProductToBasket(Basket basket, AddBasketItemRequest basketItemRequest) {
        try {
            BasketProductEvent product = productService.getProductById(basketItemRequest.getProduct());
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
            AtomicReference<BasketItems> item = new AtomicReference<>(new BasketItems());
            basketItemRepository.findByBasketAndProduct(basket, product.getId()).ifPresentOrElse(existingItem -> {
                existingItem.setQuantity(existingItem.getQuantity() + basketItemRequest.getQuantity());
                item.set(basketItemRepository.save(existingItem));
            }, () -> {
                BasketItems newItem = new BasketItems();
                newItem.setBasket(basket);
                newItem.setUuid(UUID.randomUUID().toString());
                newItem.setQuantity(basketItemRequest.getQuantity());
                newItem.setProduct(product.getId());
                item.set(basketItemRepository.save(newItem));
            });
            return new BasketItemResponse(
                    item.get().getUuid(),
                    item.get().getProduct(),
                    item.get().getQuantity(),
                    product.getPrice(),
                    product.getPrice() * item.get().getQuantity(),
                    product.getImageUrls()!=null ? product.getImageUrls().getFirst() : null
            );
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
        listBasketItemDTO.setSummaryPrice(0.0);
        listBasketItemDTO.setSummaryQuantity(0L);

        try {
            basketItemRepository.findBasketItemsByBasket(basket).forEach(item -> {
                try {
                    BasketProductEvent product = productService.getProductById(item.getProduct());

                    if (product == null) {
                        throw new ResourceNotFoundException(
                                "Product",
                                "ID",
                                item.getProduct(),
                                "PRODUCT_NOT_FOUND",
                                Map.of("productId", item.getProduct())
                        );
                    }

                    BasketItemDto basketItemDto = new BasketItemDto(
                            item.getUuid(),
                            product.getId(),
                            product.getName(),
                            item.getQuantity(),
                            product.getImageUrls()!=null ? product.getImageUrls().getFirst() : null,
                            product.getPrice(),
                            product.getPrice() * item.getQuantity(),
                            product.getIsActive()
                    );

                    listBasketItemDTO.getBasketProducts().add(basketItemDto);

                    listBasketItemDTO.setSummaryPrice(
                            listBasketItemDTO.getSummaryPrice() + (item.getQuantity() * product.getPrice())
                    );
                    listBasketItemDTO.setSummaryQuantity(
                            listBasketItemDTO.getSummaryQuantity() + item.getQuantity()
                    );

                } catch (ResourceNotFoundException e) {
                    log.error("Product not found for basket item with ID: {}", item.getProduct(), e);
                    throw e;
                } catch (Exception e) {
                    log.error("Unexpected error while processing basket item with ID: {}", item.getProduct(), e);
                    throw new ApiRequestException(
                            "An unexpected error occurred while processing basket item",
                            e,
                            "BASKET_ITEM_PROCESSING_ERROR",
                            Map.of("productId", item.getProduct())
                    );
                }
            });

            return listBasketItemDTO;

        } catch (Exception e) {
            log.error("Error while building basket item DTO for basket: {}", basket.getUuid(), e);
            throw new UnExpectedError(
                    "An unexpected error occurred while building basket item DTO",
                    e,
                    "BASKET_ITEM_DTO_BUILD_ERROR",
                    Map.of("basketId", basket.getUuid())
            );
        }
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

        try {
            basketItemRepository.findBasketItemsByBasket(basket).forEach(item -> {
                try {
                    BasketProductEvent product = productService.getProductById(item.getProduct());

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
                } catch (ResourceNotFoundException e) {
                    log.error("Product not found while building basket item event: {}", item.getProduct(), e);
                    throw e;
                } catch (Exception e) {
                    log.error("Unexpected error while processing product: {}", item.getProduct(), e);
                    throw new UnExpectedError(
                            "An unexpected error occurred while processing product",
                            e,
                            "PRODUCT_PROCESSING_ERROR",
                            Map.of("productId", item.getProduct())
                    );
                }
            });
            listBasketItemEvent.setBasketProducts(basketProducts);
            listBasketItemEvent.setSummaryPrice(summaryPrice.get());

            return listBasketItemEvent;

        } catch (Exception e) {
            log.error("Error while building basket item event for basket: {}", basket.getUuid(), e);
            throw new UnExpectedError(
                    "An unexpected error occurred while building basket item event",
                    e,
                    "BASKET_ITEM_EVENT_BUILD_ERROR",
                    Map.of("basketId", basket.getUuid())
            );
        }
    }

}
