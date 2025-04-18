package com.bacos.mokengeli.biloko.infrastructure.mapper;


import com.bacos.mokengeli.biloko.application.domain.model.DomainUser;
import com.bacos.mokengeli.biloko.infrastructure.model.Permission;
import com.bacos.mokengeli.biloko.infrastructure.model.Role;
import com.bacos.mokengeli.biloko.infrastructure.model.User;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class UserMapper {

    public DomainUser toDomain(final User user) {
        if (user == null) {
            return null;
        }

        // Transformer les rôles en List<String>
        List<String> roles = user.getRoles() != null ?
                user.getRoles().stream()
                        .map(Role::getLabel)
                        .collect(Collectors.toList()) :
                new ArrayList<>();

        // Transformer les permissions en List<String>
        List<String> permissions = user.getRoles() != null ?
                user.getRoles().stream()
                        .flatMap(role -> role.getPermissions().stream())
                        .map(Permission::getLabel)
                        .distinct()
                        .collect(Collectors.toList()) :
                new ArrayList<>();

        // Construire l'objet DomainUser
        return DomainUser.builder()
                .id(user.getId())
                .tenantId(user.getTenant().getId())
                .tenantCode(user.getTenant().getCode())
                .employeeNumber(user.getEmployeeNumber())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .postName(user.getPostName())  // Si postName est présent dans User
                .email(user.getEmail())
                .password(user.getPassword())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .roles(roles)
                .permissions(permissions)
                .build();
    }

    public DomainUser toLigthDomain(final User user) {
        if (user == null) {
            return null;
        }

        // Transformer les rôles en List<String>
        List<String> roles = user.getRoles() != null ?
                user.getRoles().stream()
                        .map(Role::getLabel)
                        .collect(Collectors.toList()) :
                new ArrayList<>();



        // Construire l'objet DomainUser
        return DomainUser.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .postName(user.getPostName())  // Si postName est présent dans User
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .roles(roles)
                .build();
    }

    public User toUser(final DomainUser domainUser) {
        if (domainUser == null) {
            return null;
        }

        return User.builder().id(domainUser.getId()).firstName(domainUser.getFirstName())
                .email(domainUser.getEmail()).employeeNumber(domainUser.getEmployeeNumber()).lastName(domainUser.getLastName())
                .postName(domainUser.getPostName()).build();
    }
}
