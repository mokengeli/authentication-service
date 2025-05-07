package com.bacos.mokengeli.biloko.application.service;

import com.bacos.mokengeli.biloko.application.domain.DomainUser;
import com.bacos.mokengeli.biloko.application.domain.model.ConnectedUser;
import com.bacos.mokengeli.biloko.application.exception.ServiceException;
import com.bacos.mokengeli.biloko.application.port.UserPort;
import com.bacos.mokengeli.biloko.config.service.CustomUserInfoDetails;
import com.bacos.mokengeli.biloko.presentation.model.UserRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class AuthenticationService {

    private final UserPort userPort;
    private final PasswordEncoder passwordEncoder;
    private final UserAppService userAppService;

    @Lazy
    public AuthenticationService(@Lazy UserPort userPort, PasswordEncoder passwordEncoder, UserAppService userAppService) {
        this.userPort = userPort;
        this.passwordEncoder = passwordEncoder;
        this.userAppService = userAppService;
    }

    public Optional<DomainUser> findUserByEmployeeNumber(String username) {
        return this.userPort.findByEmployeeNumber(username);
    }

    public DomainUser registerNewUser(UserRequest userRequest) throws ServiceException {
        try {
            DomainUser domainUser = DomainUser.builder().tenantCode(userRequest.getTenantCode())
                    .email(userRequest.getEmail()).firstName(userRequest.getFirstName()).lastName(userRequest.getLastName())
                    .postName(userRequest.getPostName())
                    .roles(List.of(userRequest.getRole())).build();

            // Crypter le mot de passe
            String hashedPassword = passwordEncoder.encode(userRequest.getPassword());

            // Mettre à jour le UserRequest avec le mot de passe crypté
            domainUser.setPassword(hashedPassword);

            // Envoyer l'utilisateur au user-service via Feign
            try {
                Optional<DomainUser> user = userPort.createUser(domainUser);
                if (user.isPresent()) {
                    DomainUser createdUser = user.get();
                    createdUser.setPassword(null);
                    createdUser.setPermissions(null);
                    createdUser.setTenantId(null);
                    return createdUser;
                }
                throw new ServiceException(UUID.randomUUID().toString(), "Can't create user. Please contact the support");

            } catch (FeignException fe) {
                String body = fe.contentUTF8();
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(body);
                // "message" est un tableau JSON, on prend le premier élément
                String onlyMessage = root.get("message").get(0).asText();
                log.error("Something went wrong when creating the user", fe);
                throw new ServiceException(UUID.randomUUID().toString(), onlyMessage);
            }
        } catch (JsonProcessingException e) {
            String uuid = UUID.randomUUID().toString();
            log.error("{} Something went wrong while parsing the feign error", uuid, e);
            throw new ServiceException(uuid, "Something went wrong while creating the user. Please contact the support");
        } catch (Exception e) {
            String uuid = UUID.randomUUID().toString();
            log.error("{} An unexpected exception occured", uuid, e);
            throw new ServiceException(uuid, "Something went wrong while creating the user. Please contact the support");

        }

    }

    public void changePassword(String oldPwd, String newPwd) throws ServiceException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserInfoDetails cu = (CustomUserInfoDetails) authentication.getPrincipal();
        String employeeNumber = cu.getUsername();
        String pwd = userPort
                .getPassword(employeeNumber).orElseThrow(() -> new ServiceException(UUID.randomUUID().toString(),
                        "Utilisateur introuvable"));

        if (!passwordEncoder.matches(oldPwd, pwd)) {
            throw new ServiceException(UUID.randomUUID().toString(),
                    "Ancien mot de passe incorrect");
        }
        try {
            userPort.updatePassword(employeeNumber, passwordEncoder.encode(newPwd));
        } catch (ServiceException d) {
            String uuid = UUID.randomUUID().toString();
            log.error("[{}]: User [{}] une erreur a été capturée",
                    uuid, cu.getEmployeeNumber(), d);
            throw new ServiceException(uuid, "Une erreur est survenue lors du changement du mot de passe ");
        }
    }
}
