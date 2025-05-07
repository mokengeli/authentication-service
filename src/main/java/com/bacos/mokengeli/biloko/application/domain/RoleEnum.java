package com.bacos.mokengeli.biloko.application.domain;

public enum RoleEnum {
    ROLE_ADMIN("Administrateur du système"),
    ROLE_USER("Utilisateur standard"),
    ROLE_MANAGER("Responsable du lounge/restaurant"),
    ROLE_SERVER("Serveur dans le lounge/restaurant"),
    ROLE_COOK("Cuisinier dans le lounge/restaurant");


    private final String label;

    RoleEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}