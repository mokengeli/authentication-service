package com.bacos.mokengeli.biloko.config.service;


import com.bacos.mokengeli.biloko.application.domain.model.ConnectedUser;
import com.bacos.mokengeli.biloko.application.service.JtiService;
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
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {


    private final String secretKey;
    private final Integer jwtExpiration;
    private final JtiService jtiService;

    @Autowired
    public JwtService(@Value("${security.jwt.secret}") String secretKey,
                      @Value("${security.jwt.expiration-time}") Integer jwtExpiration, JtiService jtiService) {
        this.secretKey = secretKey;
        this.jwtExpiration = jwtExpiration;
        this.jtiService = jtiService;
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
        if (authentication == null) return null;

        ConnectedUser domainUser = (ConnectedUser) authentication.getPrincipal();
        List<String> roles = domainUser.getRoles();

        // 1) Construis l'expiration en LocalDateTime
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        LocalDateTime expiresAt = now.plus(jwtExpiration, ChronoUnit.MILLIS);
        String platform = domainUser.getPlatformTypeEnum().name();
        // 2) Crée la session JTI avec un LocalDateTime au lieu d'Instant
        UUID jti = jtiService.createSession(
                domainUser.getEmployeeNumber(),
                platform,
                expiresAt
        );

        // 3) Génère le token en utilisant les mêmes dates (converties en Date pour le JWT)
        Date issuedAtDate = Date.from(now.atZone(ZoneOffset.UTC).toInstant());
        Date expirationDate = Date.from(expiresAt.atZone(ZoneOffset.UTC).toInstant());

        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .issuer("BACOS-TECH")
                .subject(authentication.getName())
                .claim("employeeNumber", domainUser.getEmployeeNumber())
                .claim("tenantCode", domainUser.getTenantCode())
                .claim("permissions", populateAuthorities(authentication.getAuthorities()))
                .claim("roles", roles)
                .claim("jti", jti.toString())
                .claim("appType", platform)
                .issuedAt(issuedAtDate)
                .expiration(expirationDate)
                .signWith(key)
                .compact();
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

    public String getTenantCode(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("tenantCode", String.class);
    }

    public List<String> getRoles(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("roles", List.class);
    }

    public List<String> getPermissions(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("permissions", List.class);
    }

    public UUID getJti(String token) {
        Claims claims = extractAllClaims(token);
        String jti = claims.get("jti", String.class);
        return UUID.fromString(jti);
    }
}
