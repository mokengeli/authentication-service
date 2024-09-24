package com.bacos.mokengeli.biloko.config.service;


import com.bacos.mokengeli.biloko.application.domain.model.UserInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {


    private final String secretKey;
    private final Integer jwtExpiration;

    @Autowired
    public JwtService(@Value("${security.jwt.secret}") String secretKey,
                      @Value("${security.jwt.expiration-time}") Integer jwtExpiration) {
        this.secretKey = secretKey;
        this.jwtExpiration = jwtExpiration;
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private Key getSignKey() {
        // byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateJwtToken(Authentication authentication) {
        String token = null;
        if (null != authentication) {
            UserInfo userInfo = (UserInfo) authentication.getPrincipal();
            List<String> roles = userInfo.getRoles();
            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
            token = Jwts.builder().issuer("BACOS-TECH")
                    .subject(authentication.getName())
                    .claim("tenantCode", userInfo.getTenantCode())
                    .claim("permissions", populateAuthorities(authentication.getAuthorities()))
                    .claim("roles", roles)
                    .issuedAt(new Date())
                    .expiration(new Date((new Date()).getTime() + jwtExpiration))
                    .signWith(key).compact();

        }
        return token;
    }

    private static Set<String> populateAuthorities(Collection<? extends GrantedAuthority> collection) {
        Set<String> authoritiesSet = new HashSet<>();
        for (GrantedAuthority authority : collection) {
            authoritiesSet.add(authority.getAuthority());
        }
        return authoritiesSet;
    }

    public Integer getJwtExpiration() {
        return jwtExpiration;
    }
}
