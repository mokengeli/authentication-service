package com.bacos.mokengeli.biloko.application.domain.service;

import com.bacos.mokengeli.biloko.application.domain.model.DomainUser;
import com.bacos.mokengeli.biloko.application.port.UserPort;
import com.bacos.mokengeli.biloko.exception.ServiceException;
import com.bacos.mokengeli.biloko.presentation.model.UserRequest;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthenticationService {

    private final UserPort userPort;
    private final PasswordEncoder passwordEncoder;

    @Lazy
    public AuthenticationService(@Lazy UserPort userPort, PasswordEncoder passwordEncoder) {
        this.userPort = userPort;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<DomainUser> findUserByEmployeeNumber(String username) {
        return this.userPort.findByEmployeeNumber(username);
    }

    public DomainUser registerNewUser(UserRequest userRequest) throws ServiceException {
        DomainUser domainUser = DomainUser.builder().tenantCode(userRequest.getTenantCode())
                .email(userRequest.getEmail()).firstName(userRequest.getFirstName()).lastName(userRequest.getLastName())
                .postName(userRequest.getPostName())
                .roles(List.of(userRequest.getRole())).build();
        // Crypter le mot de passe
        String hashedPassword = passwordEncoder.encode(userRequest.getPassword());

        // Mettre à jour le UserRequest avec le mot de passe crypté
        domainUser.setPassword(hashedPassword);

        // Envoyer l'utilisateur au user-service via Feign
        Optional<DomainUser> user = userPort.createUser(domainUser);
        if (user.isPresent()) {
            DomainUser createdUser = user.get();
            createdUser.setPassword(null);
            createdUser.setPermissions(null);
            createdUser.setTenantId(null);
            return createdUser;
        }
        throw new ServiceException(UUID.randomUUID().toString(), "Can't create user. Please contact the support");
    }
}
