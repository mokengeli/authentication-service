package com.bacos.mokengeli.biloko.presentation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class AuthResponseDto {
    private Long id;
    private String username;
    private String email;
    private List<String> roles;
    private List<String> permissions;
    private String firstName;
    private String lastName;
    private String postName;
    private String tenantCode;
    private String subscription;
}
