package org.example.basket.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.basket.dto.request.AddBasketItemRequest;
import org.example.basket.dto.request.DeleteItemRequest;
import org.springframework.http.ResponseEntity;

public interface BasketService {
    ResponseEntity<?> addProductToBasket(AddBasketItemRequest basketItemRequest, HttpServletRequest request, HttpServletResponse response);

    ResponseEntity<?> deleteProductFromBasket(DeleteItemRequest deleteItemRequest, HttpServletRequest request);

    ResponseEntity<?> getItems(HttpServletRequest request);
}
