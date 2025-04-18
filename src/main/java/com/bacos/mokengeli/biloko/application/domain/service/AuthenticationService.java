package com.bacos.mokengeli.biloko.application.domain.service;

import com.bacos.mokengeli.biloko.application.domain.model.DomainUser;
import com.bacos.mokengeli.biloko.application.port.UserPort;
import com.bacos.mokengeli.biloko.presentation.model.UserRequest;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {

    private final UserPort userPort;
    private final PasswordEncoder passwordEncoder;

    @Lazy
    public AuthenticationService(@Lazy UserPort userPort, PasswordEncoder passwordEncoder) {
        this.userPort = userPort;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<DomainUser> findUserByUserName(String username) {
        return this.userPort.findByUserName(username);
    }

    public void registerNewUser(UserRequest userRequest) {
        DomainUser domainUser = DomainUser.builder().tenantId(userRequest.getTenantId())
                .email(userRequest.getEmail()).firstName(userRequest.getFirstName()).lastName(userRequest.getLastName())
                .postName(userRequest.getPostName()).build();
        // Crypter le mot de passe
        String hashedPassword = passwordEncoder.encode(userRequest.getPassword());

        // Mettre à jour le UserRequest avec le mot de passe crypté
        domainUser.setPassword(hashedPassword);

        // Envoyer l'utilisateur au user-service via Feign
        userPort.createUser(domainUser);
    }
}
