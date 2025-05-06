package com.bacos.mokengeli.biloko.config.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class CookieService {


    private final boolean isSsl;
    private final JwtService jwtService;

    @Autowired
    public CookieService(@Value("${security.is-ssl}") boolean isSsl, JwtService jwtService) {
        this.isSsl = isSsl;
        this.jwtService = jwtService;
    }

    public void addNewAccessTokenToResponse(HttpServletResponse response, Authentication authentication) {
        String accessToken = this.jwtService.generateJwtToken(authentication);
        Cookie cookie = new Cookie("accessToken", accessToken);
        Integer jwtExpiration = this.jwtService.getJwtExpiration();
        cookie.setMaxAge(jwtExpiration / 1000);
        cookie.setHttpOnly(true);
        cookie.setSecure(isSsl);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public void clearAccessTokenFromResponse(HttpServletRequest request, HttpServletResponse response) {
        // Invalider la session HTTP
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Remove accessToken cookie
        Cookie cookie = new Cookie("accessToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(isSsl); // Set this only if you're using HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }


}
