package org.example.auth.service;

import jakarta.servlet.http.Cookie;

public interface CookieService {
    Cookie generateCookie(String name, String value, int exp);

    Cookie removeCookie(Cookie[] cookies, String name);
}
