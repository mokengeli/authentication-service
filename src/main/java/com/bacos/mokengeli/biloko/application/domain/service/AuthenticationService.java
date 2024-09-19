package com.bacos.mokengeli.biloko.application.domain.service;

import com.bacos.mokengeli.biloko.application.domain.model.UserInfo;
import com.bacos.mokengeli.biloko.application.port.UserPort;
import com.bacos.mokengeli.biloko.presentation.model.UserRequest;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
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

    public Optional<UserInfo> findUserByUserName(String username) {
        return this.userPort.findByUserName(username);
    }

    public void registerNewUser(UserRequest userRequest) {
        UserInfo userInfo = UserInfo.builder().tenantId(userRequest.getTenantId())
                .email(userRequest.getEmail()).firstName(userRequest.getFirstName()).lastName(userRequest.getLastName())
                .postName(userRequest.getPostName()).build();
        // Crypter le mot de passe
        String hashedPassword = passwordEncoder.encode(userRequest.getPassword());

        // Mettre à jour le UserRequest avec le mot de passe crypté
        userInfo.setPassword(hashedPassword);

        // Envoyer l'utilisateur au user-service via Feign
        userPort.createUser(userInfo);
    }
}
