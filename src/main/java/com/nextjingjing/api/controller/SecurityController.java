package com.nextjingjing.api.controller;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecurityController {

    @GetMapping("/api/security/token")
    public CsrfToken csrf(CsrfToken token) {
        return token;
    }
}
