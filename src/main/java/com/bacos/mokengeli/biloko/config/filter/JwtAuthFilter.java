package com.bacos.mokengeli.biloko.config.filter;


import com.bacos.mokengeli.biloko.application.domain.model.ConnectedUser;
import com.bacos.mokengeli.biloko.config.service.CustomUserInfoDetails;
import com.bacos.mokengeli.biloko.config.service.JwtService;
import com.bacos.mokengeli.biloko.config.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private JwtService jwtService;
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    public JwtAuthFilter(JwtService jwtService, UserDetailsServiceImpl userDetailsServiceImpl) {
        this.jwtService = jwtService;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = null;
        String username = null;


        String path = request.getServletPath();

        // Skip JWT checks on login (and any other public endpoints)
        if ("/api/auth/login".equals(path)) {
            filterChain.doFilter(request, response);
            return;
        }
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("accessToken")) {
                    token = cookie.getValue();
                    username = jwtService.extractUsername(token);
                }
            }
        }
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            CustomUserInfoDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);
            if (jwtService.validateToken(token, userDetails)) {
                String tenantCode = this.jwtService.getTenantCode(token);
                List<String> roles = this.jwtService.getRoles(token);
                List<String> permissions = this.jwtService.getPermissions(token);
                UUID jti = this.jwtService.getJti(token);
                List<GrantedAuthority> grantedAuthorities = getAuthoritiesFromJWT(permissions);
                ConnectedUser connectedUser = createUser(username, userDetails.getEmployeeNumber(),
                        tenantCode, roles, permissions, jti);
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(connectedUser, null, grantedAuthorities);
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

        }

        filterChain.doFilter(request, response);
    }

    private ConnectedUser createUser(String username, String employeeNumber, String tenantCode, List<String> roles,
                                     List<String> permissions, UUID jti) {
        return ConnectedUser.builder()
                .username(username)
                .employeeNumber(employeeNumber)
                .tenantCode(tenantCode)
                .roles(roles)
                .permissions(permissions)
                .jti(jti)
                .build();
    }

    public List<GrantedAuthority> getAuthoritiesFromJWT(List<String> authorities) {
        return authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

}