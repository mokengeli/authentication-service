package com.bacos.mokengeli.biloko.presentation;

import com.bacos.mokengeli.biloko.application.domain.DomainUser;
import com.bacos.mokengeli.biloko.application.service.AuthenticationService;
import com.bacos.mokengeli.biloko.config.service.CookieService;
import com.bacos.mokengeli.biloko.config.service.CustomUserInfoDetails;
import com.bacos.mokengeli.biloko.application.exception.ServiceException;
import com.bacos.mokengeli.biloko.presentation.exception.ResponseStatusWrapperException;
import com.bacos.mokengeli.biloko.presentation.model.AuthResponseDto;
import com.bacos.mokengeli.biloko.presentation.model.ChangePasswordRequest;
import com.bacos.mokengeli.biloko.presentation.model.LoginRequest;
import com.bacos.mokengeli.biloko.presentation.model.UserRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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
    public ResponseEntity<AuthResponseDto> authenticateUser(HttpServletResponse response, @RequestBody LoginRequest loginRequest) {
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
    public ResponseEntity<AuthResponseDto> getCurrentUserInfo() {
        // Récupération de l'Authentication depuis le SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new ResponseStatusWrapperException(HttpStatus.UNAUTHORIZED, "user not authentify");
        }

        // Extraction du principal qui doit être de type CustomUserInfoDetails
        CustomUserInfoDetails principal = (CustomUserInfoDetails) authentication.getPrincipal();

        // Recherche des informations complètes de l'utilisateur via le service
        Optional<DomainUser> optUser = authenticationService.findUserByEmployeeNumber(principal.getUsername());
        if (optUser.isEmpty()) {
            throw new ResponseStatusWrapperException(HttpStatus.BAD_REQUEST, "User not found");
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

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {


        // Nettoyer le cookie (par ex. enlever JSESSIONID ou ton propre cookie d'authentification)
        cookieService.clearAccessTokenFromResponse(request, response);

        // Nettoyer le SecurityContext
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok("Déconnexion réussie");
    }

    @PatchMapping("/password")
    public void changePassword(@Valid @RequestBody ChangePasswordRequest req) {
        try {
            this.authenticationService.changePassword(req.getOldPassword(), req.getNewPassword());
        } catch (ServiceException e) {
            throw new ResponseStatusWrapperException(
                    HttpStatus.BAD_REQUEST, e.getMessage(), e.getTechnicalId());
        }
    }

}
