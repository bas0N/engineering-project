package org.example.basket.service;

import jakarta.servlet.http.Cookie;

public interface CookieService {
    Cookie generateCookie(String key, String value);
}
