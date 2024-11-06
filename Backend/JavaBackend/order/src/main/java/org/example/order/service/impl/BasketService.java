package org.example.order.service.impl;

import jakarta.servlet.http.Cookie;
import org.example.order.dto.ListBasketItemDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class BasketService {
//    @Value("${basket.service}")
//    private String BASKET_URL;
    public ListBasketItemDto getBasket(Cookie value) {
        //tutaj kafka wale do koszyka
        return null;
    }

    public void removeBasket(Cookie value,String uuid) {
        //tutaj kafka wale do koszyka
    }
}
