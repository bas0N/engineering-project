package org.example.basket.service.impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.basket.dto.BasketItemDto;
import org.example.basket.dto.ListBasketItemDto;
import org.example.basket.dto.Product;
import org.example.basket.dto.request.AddBasketItemRequest;
import org.example.basket.entity.Basket;
import org.example.basket.entity.BasketItems;
import org.example.basket.repository.BasketItemRepository;
import org.example.basket.repository.BasketRepository;
import org.example.basket.service.BasketService;
import org.example.basket.service.CookieService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasketServiceImpl implements BasketService {
    private final BasketRepository basketRepository;
    private final BasketItemRepository basketItemRepository;
    private final CookieService cookieService;
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
                        saveProductToBasket(basket, AddBasketItemRequest);
                        Long sum = basketItemRepository.sumBasketItems(basket.getId());
                        if (sum == null) sum = 0L;
                        httpHeaders.add("X-Total-Count", String.valueOf(sum));
                    }, () -> {
                        Basket basket = createBasket();
                        response.addCookie(cookieService.generateCookie("basket", basket.getUuid()));
                        saveProductToBasket(basket, AddBasketItemRequest);
                        Long sum = basketItemRepository.sumBasketItems(basket.getId());
                        if (sum == null) sum = 0L;
                        httpHeaders.add("X-Total-Count", String.valueOf(sum));
                    });
                }, () -> {
                    Basket basket = createBasket();
                    response.addCookie(cookieService.generateCookie("basket", basket.getUuid()));
                    saveProductToBasket(basket, AddBasketItemRequest);
                    Long sum = basketItemRepository.sumBasketItems(basket.getId());
                    if (sum == null) sum = 0L;
                    httpHeaders.add("X-Total-Count", String.valueOf(sum));
                });
        return ResponseEntity.ok().headers(httpHeaders).body("Successful add item to basket");
    }

    @Override
    public ResponseEntity<?> deleteProductFromBasket(String uuid, HttpServletRequest request) {
        HttpHeaders httpHeaders = new HttpHeaders();
        List<Cookie> cookies = new ArrayList<>();
        if (request.getCookies() != null) {
            cookies.addAll(List.of(request.getCookies()));
        }
        cookies.stream().filter(value -> value.getName().equals("basket"))
                .findFirst().ifPresentOrElse(value -> {
                    basketRepository.findByUuid(value.getValue()).ifPresentOrElse(basket -> {
                        deleteItem(uuid,basket);
                        Long sum = basketItemRepository.sumBasketItems(basket.getId());
                        if (sum == null) sum = 0L;
                        httpHeaders.add("X-Total-Count", String.valueOf(sum));
                    }, () -> {
                        //throw new NoBasketInfoException("Basket doesn't exist");
                    });
                }, () -> {
                    //throw new NoBasketInfoException("No basket info in request");
                });
        return ResponseEntity.ok().headers(httpHeaders).body("Successful delete item from basket");
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
        cookies.stream().filter(value -> value.getName().equals("basket"))
                .findFirst().ifPresentOrElse(value->{
                    Basket basket = basketRepository.findByUuid(value.getValue()).orElseThrow();
                    Long sum = basketItemRepository.sumBasketItems(basket.getId());
                    if (sum == null) sum = 0L;
                    httpHeaders.add("X-Total-Count", String.valueOf(sum));
                    basketItemRepository.findBasketItemsByBasket(basket).forEach(item->{
                        try {
                            Product product = getProduct(item.getProduct());
                            listBasketItemDTO.getBasketProducts().add(new BasketItemDto(product.getUuid(),
                                    product.getName(),
                                    item.getQuantity(),
                                    product.getImageUrls()[0],
                                    product.getPrice(),
                                    product.getPrice() * item.getQuantity()));
                            listBasketItemDTO.setSummaryPrice(listBasketItemDTO.getSummaryPrice()+ (item.getQuantity()*product.getPrice()));
                        } catch (URISyntaxException e) {
                            throw new RuntimeException(e);
                        }
                    });
                },()->{
                    //throw new NoBasketInfoException("No basket info in request");
                });
        if (httpHeaders.isEmpty()) httpHeaders.add("X-Total-Count", String.valueOf(0));
        return ResponseEntity.ok().headers(httpHeaders).body(listBasketItemDTO);
    }

    private void deleteItem(String uuid, Basket basket) {
        basketItemRepository.findBasketItemsByProductAndBasket(uuid,basket).ifPresentOrElse(basketItemRepository::delete,()->{
            //throw new BasketItemDontExistException("Basket item dont exist");
        });
        Long sum = basketItemRepository.sumBasketItems(basket.getId());
        if (sum==null || sum == 0){
            basketRepository.delete(basket);
        }
    }

    private Basket createBasket(){
        Basket basket = new Basket();
        basket.setUuid(UUID.randomUUID().toString());
        return basketRepository.saveAndFlush(basket);
    }

    private void saveProductToBasket(Basket basket, AddBasketItemRequest basketItemRequest) {
        BasketItems basketItems = new BasketItems();
        try{
            Product product = getProduct(basketItemRequest.getProduct());
            if(product!=null){
                basketItemRepository.findByBasketAndProduct(basket, product.getUuid()).ifPresentOrElse(basketItems1 -> {
                    basketItems1.setQuantity(basketItems1.getQuantity() + basketItemRequest.getQuantity());
                    basketItemRepository.save(basketItems1);
                }, () -> {
                    basketItems.setBasket(basket);
                    basketItems.setUuid(UUID.randomUUID().toString());
                    basketItems.setQuantity(basketItemRequest.getQuantity());
                    basketItems.setProduct(product.getUuid());
                    basketItemRepository.save(basketItems);
                });
            }
        } catch(URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
