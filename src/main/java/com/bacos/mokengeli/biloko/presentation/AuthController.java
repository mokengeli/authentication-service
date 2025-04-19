package com.bacos.mokengeli.biloko.presentation;

import com.bacos.mokengeli.biloko.application.domain.model.DomainUser;
import com.bacos.mokengeli.biloko.application.domain.service.AuthenticationService;
import com.bacos.mokengeli.biloko.config.service.CookieService;
import com.bacos.mokengeli.biloko.config.service.CustomUserInfoDetails;
import com.bacos.mokengeli.biloko.presentation.exception.ResponseStatusWrapperException;
import com.bacos.mokengeli.biloko.presentation.model.AuthResponseDto;
import com.bacos.mokengeli.biloko.presentation.model.LoginRequest;
import com.bacos.mokengeli.biloko.presentation.model.UserRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationService authenticationService;

    private final AuthenticationManager authenticationManager;

    private final CookieService cookieService;

    @Autowired
    public AuthController(AuthenticationService authenticationService, AuthenticationManager authenticationManager,
                          CookieService cookieService) {
        this.authenticationService = authenticationService;
        this.authenticationManager = authenticationManager;
        this.cookieService = cookieService;
    }

    @PostMapping("/register")
    public ResponseEntity<DomainUser> registerUser(@RequestBody UserRequest userRequest) {
        try {
            DomainUser domainUser = authenticationService.registerNewUser(userRequest);
            return ResponseEntity.ok(domainUser);
        } catch (Exception e) {
            throw new ResponseStatusWrapperException(HttpStatus.BAD_REQUEST, "Creation failed: " + e.getMessage());
        }

    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(HttpServletResponse response, @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            CustomUserInfoDetails principal = (CustomUserInfoDetails) authentication.getPrincipal();
            SecurityContextHolder.getContext().setAuthentication(authentication);
            this.cookieService.addNewAccessTokenToResponse(response, authentication);

            Optional<DomainUser> OptUser = authenticationService.findUserByEmployeeNumber(principal.getUsername());
            //List<String> permissions = principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
            DomainUser user = OptUser.get();
            // List<String> roles = user.getRoles();
            return ResponseEntity.ok(AuthResponseDto.builder()
                    .id(user.getId()).email(user.getEmail())
                    .firstName(user.getFirstName()).lastName(user.getLastName())
                    .postName(user.getPostName()).permissions(user.getPermissions())
                    .tenantCode(user.getTenantCode())
                    .roles(user.getRoles()).build());
        } catch (Exception e) {
            throw new ResponseStatusWrapperException(HttpStatus.BAD_REQUEST, "Login failed: " + e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUserInfo() {
        // Récupération de l'Authentication depuis le SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur non authentifié");
        }

        // Extraction du principal qui doit être de type CustomUserInfoDetails
        CustomUserInfoDetails principal = (CustomUserInfoDetails) authentication.getPrincipal();

        // Recherche des informations complètes de l'utilisateur via le service
        Optional<DomainUser> optUser = authenticationService.findUserByEmployeeNumber(principal.getUsername());
        if (optUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur introuvable");
        }

        DomainUser user = optUser.get();

        // Construction de la réponse basée sur AuthResponseDto
        AuthResponseDto responseDto = AuthResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .postName(user.getPostName())
                .permissions(user.getPermissions())
                .tenantCode(user.getTenantCode())
                .roles(user.getRoles())
                .build();

        return ResponseEntity.ok(responseDto);
    }


}
