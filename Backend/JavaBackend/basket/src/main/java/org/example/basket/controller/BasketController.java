package org.example.basket.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.basket.dto.request.AddBasketItemRequest;
import org.example.basket.dto.request.DeleteItemRequest;
import org.example.basket.service.BasketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/basket")
public class BasketController {

    private final BasketService basketService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> addProductToBasket(@RequestBody @Valid AddBasketItemRequest basketItemRequest, HttpServletRequest request, HttpServletResponse response) {
        return basketService.addProductToBasket(basketItemRequest, request, response);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteProductFromBasket(@RequestBody @Valid DeleteItemRequest deleteItemRequest, HttpServletRequest request) {
        return basketService.deleteProductFromBasket(deleteItemRequest, request);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getItems(HttpServletRequest request) {
        return basketService.getItems(request);
    }
}
