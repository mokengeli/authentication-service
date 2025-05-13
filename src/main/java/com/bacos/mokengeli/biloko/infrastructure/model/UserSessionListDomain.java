package com.bacos.mokengeli.biloko.infrastructure.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DTOs pour la désérialisation de la réponse "liste"
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSessionListDomain {
    private String employeeNumber;
    private String appType;
    private Integer maxSessions;
    private List<UserSessionDomain> sessions;

    public List<String> extractJtis() {
        return sessions == null ? List.of() : sessions.stream()
                .map(UserSessionDomain::getJti)
                .collect(Collectors.toList());
    }
}
