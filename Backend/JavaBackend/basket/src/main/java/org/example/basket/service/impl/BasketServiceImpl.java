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
    private final CookieService cookieService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final BasketKafkaConsumer basketKafkaConsumer;
    private final BasketKafkaProducer basketKafkaProducer;

    @Override
    public ResponseEntity<?> addProductToBasket(AddBasketItemRequest basketItemRequest, HttpServletRequest request, HttpServletResponse response) {
        HttpHeaders httpHeaders = new HttpHeaders();
        List<Cookie> cookies = new ArrayList<>();
        if (request.getCookies() != null) {
            cookies.addAll(List.of(request.getCookies()));
        }
        cookies.stream().filter(value -> value.getName().equals("basket"))
                .findFirst().ifPresentOrElse(value -> {
                    basketRepository.findByUuid(value.getValue()).ifPresentOrElse(basket -> {
                        ProcessBasket(basketItemRequest, basket, httpHeaders);
                    }, () -> {
                        Basket basket = createBasket();
                        response.addCookie(cookieService.generateCookie("basket", basket.getUuid()));
                        ProcessBasket(basketItemRequest, basket, httpHeaders);
                    });
                }, () -> {
                    Basket basket = createBasket();
                    response.addCookie(cookieService.generateCookie("basket", basket.getUuid()));
                    ProcessBasket(basketItemRequest, basket, httpHeaders);
                });
        return ResponseEntity.ok().headers(httpHeaders).body("Successful add item to basket");
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
        try{
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
        }catch (TimeoutException e) {
            throw new ApiRequestException("Timeout while retrieving product details for product ID: " + basketItemRequest.getProduct());
        } catch (Exception e) {
            throw new ApiRequestException("An unexpected error occurred while retrieving product details for product ID: " + basketItemRequest.getProduct());
        }
    }

    @Override
    public ResponseEntity<?> deleteProductFromBasket(DeleteItemRequest deleteItemRequest, HttpServletRequest request) {
        HttpHeaders httpHeaders = new HttpHeaders();

        List<Cookie> cookies = request.getCookies() != null ? List.of(request.getCookies()) : new ArrayList<>();

        String basketUuid = cookies.stream()
                .filter(cookie -> cookie.getName().equals("basket"))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow(() -> new NoBasketInfoException("No basket info in request"));

        Basket basket = basketRepository.findByUuid(basketUuid)
                .orElseThrow(() -> new NoBasketInfoException("Basket doesn't exist"));

        deleteItem(deleteItemRequest, basket);

        Long sum = basketItemRepository.sumBasketItems(basket.getId());
        if (sum == null) {
            sum = 0L;
        }
        httpHeaders.add("X-Total-Count", String.valueOf(sum));

        return ResponseEntity.ok().headers(httpHeaders).body("Successfully deleted item from basket");
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
        List<Cookie> cookies = new ArrayList<>();
        HttpHeaders httpHeaders = new HttpHeaders();
        if (request.getCookies() != null) {
            cookies.addAll(List.of(request.getCookies()));
        }
        ListBasketItemDto listBasketItemDTO = new ListBasketItemDto();
        listBasketItemDTO.setBasketProducts(new ArrayList<>());

        cookies.stream()
                .filter(cookie -> "basket".equals(cookie.getName()))
                .findFirst()
                .ifPresentOrElse(cookie -> {
                    Basket basket = basketRepository.findByUuid(cookie.getValue())
                            .orElseThrow(() -> new NoBasketInfoException("Basket doesn't exist"));

                    Long sum = basketItemRepository.sumBasketItems(basket.getId());
                    if (sum == null) sum = 0L;
                    httpHeaders.add("X-Total-Count", String.valueOf(sum));

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
                }, () -> {
                    throw new NoBasketInfoException("No basket info in request");
                });

        if (httpHeaders.isEmpty()) {
            httpHeaders.add("X-Total-Count", String.valueOf(0));
        }
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


    private Basket createBasket() {
        Basket basket = new Basket();
        basket.setUuid(UUID.randomUUID().toString());
        return basketRepository.saveAndFlush(basket);
    }
}
