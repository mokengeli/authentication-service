package com.bacos.mokengeli.biloko.presentation;

import com.bacos.mokengeli.biloko.application.domain.model.UserInfo;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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
    public ResponseEntity<Void> registerUser(@RequestBody UserRequest userRequest) {
        authenticationService.registerNewUser(userRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
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

            Optional<UserInfo> OptUser = authenticationService.findUserByUserName(principal.getUsername());
            //List<String> permissions = principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
            UserInfo user = OptUser.get();
           // List<String> roles = user.getRoles();
            return ResponseEntity.ok(AuthResponseDto.builder()
                    .id(user.getId()).email(user.getEmail())
                    .firstName(user.getFirstName()).lastName(user.getLastName())
                    .postName(user.getPostName()).permissions(user.getPermissions())
                    .roles(user.getRoles()).build());
        } catch (Exception e) {
            throw new ResponseStatusWrapperException(HttpStatus.BAD_REQUEST, "Login failed: " + e.getMessage());
        }
    }


}
