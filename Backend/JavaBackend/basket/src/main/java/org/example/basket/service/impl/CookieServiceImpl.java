package org.example.basket.service.impl;

import jakarta.servlet.http.Cookie;
import org.example.basket.service.CookieService;
import org.springframework.stereotype.Service;

@Service
public class CookieServiceImpl implements CookieService {
    @Override
    public Cookie generateCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 365);
        cookie.setHttpOnly(true);
        return cookie;
    }
}
