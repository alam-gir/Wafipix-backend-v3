package com.wafipix.wafipix.common.security.service.impl;

import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Service;

import com.wafipix.wafipix.common.security.service.CookieService;

@Service
public class CookieServiceImpl implements CookieService {

    @Override
    public Cookie create(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setDomain(".wafipix.com");
        return cookie;
    }
}
