package org.example.basket.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.basket.dto.request.AddBasketItemRequest;
import org.example.basket.dto.request.DeleteItemRequest;
import org.example.basket.dto.response.BasketItemResponse;
import org.example.commondto.ListBasketItemEvent;
import org.springframework.http.ResponseEntity;

public interface BasketService {
    ResponseEntity<BasketItemResponse> addProductToBasket(AddBasketItemRequest basketItemRequest, HttpServletRequest request, HttpServletResponse response);

    ResponseEntity<?> deleteProductFromBasket(DeleteItemRequest deleteItemRequest, HttpServletRequest request);

    ResponseEntity<?> getItems(HttpServletRequest request);

    void removeBasketById(String basketId);

    ListBasketItemEvent getBasketItems(String basketId);
}
