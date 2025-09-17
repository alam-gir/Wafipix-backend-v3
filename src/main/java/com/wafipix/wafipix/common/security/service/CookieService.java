package com.wafipix.wafipix.common.security.service;

import jakarta.servlet.http.Cookie;

public interface CookieService {
    public Cookie create(String name, String value);
}
