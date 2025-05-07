package com.bacos.mokengeli.biloko.application.service;

import com.bacos.mokengeli.biloko.application.domain.RoleEnum;
import com.bacos.mokengeli.biloko.application.domain.model.ConnectedUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.bacos.mokengeli.biloko.application.domain.RoleEnum.*;


@Service
public class UserAppService {

    public ConnectedUser getConnectedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (ConnectedUser) authentication.getPrincipal();
    }

    public boolean isAdminUser() {
        ConnectedUser connectedUser = getConnectedUser();
        return connectedUser.getRoles().contains(ROLE_ADMIN.name());
    }

    public boolean isManagerUser() {
        ConnectedUser connectedUser = getConnectedUser();
        return connectedUser.getRoles().contains(ROLE_MANAGER.name());
    }

    public RoleEnum getMainRole() {
        ConnectedUser connectedUser = getConnectedUser();
        List<String> roles = connectedUser.getRoles();

        if (roles.contains(ROLE_ADMIN.name())) {
            return ROLE_ADMIN;
        }
        if (roles.contains(ROLE_MANAGER.name())) {
            return ROLE_MANAGER;
        }
        if (roles.contains(ROLE_SERVER.name())) {
            return ROLE_SERVER;
        }
        if (roles.contains(ROLE_COOK.name())) {
            return ROLE_COOK;
        }
        throw new IllegalArgumentException("Role not authorized");
    }


}
