package com.bacos.mokengeli.biloko.application.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DomainUser {
    private Long id;
    private Long tenantId;
    private String tenantCode;
    private String employeeNumber;
    private String firstName;
    private String lastName;
    private String postName;
    private String email;
    private String password;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> roles;
    private List<String> permissions;

}
