package com.bacos.mokengeli.biloko.presentation;

import com.bacos.mokengeli.biloko.application.domain.DomainUser;
import com.bacos.mokengeli.biloko.application.domain.PlatformTypeEnum;
import com.bacos.mokengeli.biloko.application.domain.model.ConnectedUser;
import com.bacos.mokengeli.biloko.application.service.AuthenticationService;
import com.bacos.mokengeli.biloko.application.service.JtiService;
import com.bacos.mokengeli.biloko.application.service.UserAppService;
import com.bacos.mokengeli.biloko.config.service.CookieService;
import com.bacos.mokengeli.biloko.application.exception.ServiceException;
import com.bacos.mokengeli.biloko.infrastructure.model.UserSessionListDomain;
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

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationService authenticationService;

    private final AuthenticationManager authenticationManager;

    private final CookieService cookieService;
    private final JtiService jtiService;
    private final UserAppService userAppService;

    @Autowired
    public AuthController(AuthenticationService authenticationService, AuthenticationManager authenticationManager,
                          CookieService cookieService, JtiService jtiService, UserAppService userAppService) {
        this.authenticationService = authenticationService;
        this.authenticationManager = authenticationManager;
        this.cookieService = cookieService;
        this.jtiService = jtiService;
        this.userAppService = userAppService;
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
            ConnectedUser principal = (ConnectedUser) authentication.getPrincipal();

            String platformType = loginRequest.getPlatformType();
            PlatformTypeEnum[] values = PlatformTypeEnum.values();
            if (values.length == 0 || !Arrays.asList(values).contains(PlatformTypeEnum.valueOf(platformType))) {
                throw new ResponseStatusWrapperException(HttpStatus.BAD_REQUEST, "Login failed: Plateform type not found");
            }
            principal.setPlatformTypeEnum(PlatformTypeEnum.valueOf(platformType));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = this.cookieService.addNewAccessTokenToResponse(response, authentication);

            Optional<DomainUser> OptUser = authenticationService.findUserByEmployeeNumber(principal.getEmployeeNumber());
            DomainUser user = OptUser.get();
            return ResponseEntity.ok(AuthResponseDto.builder()
                    .id(user.getId()).email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .userName(user.getUserName())
                    .postName(user.getPostName())
                    .permissions(user.getPermissions())
                    .tenantCode(user.getTenantCode())
                    .tenantName(user.getTenantName())
                    .employeeNumber(user.getEmployeeNumber())
                    .createdAt(user.getCreatedAt())
                    .subscriptionCode(user.getTenantSubscriptionPlan().getCode())
                    .establishmentCode(user.getTenantEstablishmentType().getCode())
                    .token(token)
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
        ConnectedUser principal = (ConnectedUser) authentication.getPrincipal();

        // Recherche des informations complètes de l'utilisateur via le service
        Optional<DomainUser> optUser = authenticationService.findUserByEmployeeNumber(principal.getEmployeeNumber());
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
                .userName(user.getUserName())
                .permissions(user.getPermissions())
                .tenantCode(user.getTenantCode())
                .tenantName(user.getTenantName())
                .employeeNumber(user.getEmployeeNumber())
                .createdAt(user.getCreatedAt())
                .subscriptionCode(user.getTenantSubscriptionPlan().getCode())
                .establishmentCode(user.getTenantEstablishmentType().getCode())
                .roles(user.getRoles())
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        ConnectedUser user = userAppService.getConnectedUser();
        jtiService.invalidateSession(user.getJti());

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

    @GetMapping("/internal/jti")
    public UserSessionListDomain introspectJti(
            @RequestParam String employeeNumber,
            @RequestParam String appType
    ) {
        return jtiService.getActiveJti(employeeNumber, appType);

    }

}
