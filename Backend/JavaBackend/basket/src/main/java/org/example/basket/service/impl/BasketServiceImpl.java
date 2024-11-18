package org.example.basket.service.impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.apache.kafka.common.errors.TimeoutException;
import org.example.basket.dto.BasketItemDto;
import org.example.basket.dto.ListBasketItemDto;
import org.example.basket.dto.Product;
import org.example.basket.dto.request.AddBasketItemRequest;
import org.example.basket.dto.request.DeleteItemRequest;
import org.example.basket.entity.Basket;
import org.example.basket.entity.BasketItems;
import org.example.basket.kafka.BasketKafkaConsumer;
import org.example.basket.kafka.BasketKafkaProducer;
import org.example.basket.repository.BasketItemRepository;
import org.example.basket.repository.BasketRepository;
import org.example.basket.service.BasketService;
import org.example.basket.service.CookieService;
import org.example.commondto.BasketProductEvent;
import org.example.commondto.LikeEvent;
import org.example.exception.exceptions.ApiRequestException;
import org.example.exception.exceptions.NoBasketInfoException;
import org.example.jwtcommon.jwt.JwtCommonService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class BasketServiceImpl implements BasketService {
    private final BasketRepository basketRepository;
    private final BasketItemRepository basketItemRepository;
    private final BasketKafkaConsumer basketKafkaConsumer;
    private final BasketKafkaProducer basketKafkaProducer;
    private final JwtCommonService jwtCommonService;

    @Override
    public ResponseEntity<?> addProductToBasket(AddBasketItemRequest basketItemRequest, HttpServletRequest request, HttpServletResponse response) {
        HttpHeaders httpHeaders = new HttpHeaders();

        Basket basket = getOrCreateBasket(request);
        ProcessBasket(basketItemRequest, basket, httpHeaders);

        return ResponseEntity.ok().headers(httpHeaders).body(basket.getUuid());
    }

    private Basket getOrCreateBasket(HttpServletRequest request) {
        String userId = jwtCommonService.getUserFromRequest(request);
        return basketRepository.findByOwnerId(userId)
                .orElseGet(() -> {
                    Basket newBasket = new Basket();
                    newBasket.setUuid(UUID.randomUUID().toString());
                    newBasket.setOwnerId(userId);
                    return basketRepository.saveAndFlush(newBasket);
                });
    }

    private void ProcessBasket(AddBasketItemRequest basketItemRequest, Basket basket, HttpHeaders httpHeaders) {
        saveProductToBasket(basket, basketItemRequest);
        Long sum = basketItemRepository.sumBasketItems(basket.getId());
        if (sum == null) sum = 0L;
        httpHeaders.add("X-Total-Count", String.valueOf(sum));
    }

    private void saveProductToBasket(Basket basket, AddBasketItemRequest basketItemRequest) {
        CompletableFuture<BasketProductEvent> productFuture = basketKafkaConsumer.getProductDetails(basketItemRequest.getProduct());
        basketKafkaProducer.requestProductDetails(basketItemRequest.getProduct());
        try {
            BasketProductEvent product = productFuture.get(30, TimeUnit.SECONDS);
            if (product != null) {
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
            } else {
                throw new ResourceNotFoundException("Product not found with ID: " + basketItemRequest.getProduct());
            }
        } catch (TimeoutException e) {
            throw new ApiRequestException("Timeout while retrieving product details for product ID: " + basketItemRequest.getProduct());
        } catch (Exception e) {
            throw new ApiRequestException("An unexpected error occurred while retrieving product details for product ID: " + basketItemRequest.getProduct());
        }
    }

    @Override
    public ResponseEntity<?> deleteProductFromBasket(DeleteItemRequest deleteItemRequest, HttpServletRequest request) {
        HttpHeaders httpHeaders = new HttpHeaders();

        String userId = jwtCommonService.getUserFromRequest(request);
        Basket basket = basketRepository.findByOwnerId(userId)
                .orElseThrow(() -> new NoBasketInfoException("Basket doesn't exist"));

        deleteItem(deleteItemRequest, basket);

        Long sum = basketItemRepository.sumBasketItems(basket.getId());
        if (sum == null) sum = 0L;
        httpHeaders.add("X-Total-Count", String.valueOf(sum));

        return ResponseEntity.ok().headers(httpHeaders).body(basket);
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
                    throw new ResourceNotFoundException("Basket item not found with UUID: " + basketItemUuid);
                });

        Long sum = basketItemRepository.sumBasketItems(basket.getId());
        if (sum == null || sum == 0) {
            basketRepository.delete(basket);
        }
    }

    @Override
    public ResponseEntity<?> getItems(HttpServletRequest request) {
        String userId = jwtCommonService.getUserFromRequest(request);
        Basket basket = basketRepository.findByOwnerId(userId)
                .orElseThrow(() -> new NoBasketInfoException("Basket doesn't exist"));

        ListBasketItemDto listBasketItemDTO = new ListBasketItemDto();
        listBasketItemDTO.setBasketProducts(new ArrayList<>());

        Long sum = basketItemRepository.sumBasketItems(basket.getId());
        if (sum == null) sum = 0L;

        basketItemRepository.findBasketItemsByBasket(basket).forEach(item -> {
            BasketProductEvent product = getProductDetails(item.getProduct());

            if (product != null) {
                listBasketItemDTO.getBasketProducts().add(new BasketItemDto(
                        product.getId(),
                        product.getName(),
                        item.getQuantity(),
                        product.getImageUrls().getFirst(),
                        product.getPrice(),
                        product.getPrice() * item.getQuantity()
                ));
                listBasketItemDTO.setSummaryPrice(
                        listBasketItemDTO.getSummaryPrice() + (item.getQuantity() * product.getPrice())
                );
            } else {
                throw new ResourceNotFoundException("Product not found with ID: " + item.getProduct());
            }
        });

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Total-Count", String.valueOf(sum));

        return ResponseEntity.ok().headers(httpHeaders).body(listBasketItemDTO);
    }

    private BasketProductEvent getProductDetails(String productId) {
        CompletableFuture<BasketProductEvent> productFuture = basketKafkaConsumer.getProductDetails(productId);
        basketKafkaProducer.requestProductDetails(productId);
        try {
            return productFuture.get(30, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            throw new ApiRequestException("Timeout while retrieving product details for product ID: " + productId);
        } catch (Exception e) {
            throw new ApiRequestException("An unexpected error occurred while retrieving product details for product ID: " + productId);
        }
    }
}
