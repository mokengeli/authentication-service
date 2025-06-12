package com.bacos.mokengeli.biloko.presentation.model;

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
public class UserRequest {
    private String tenantCode;
    private String firstName;
    private String lastName;
    private String postName;
    private String userName;
    private String email;
    private String password;
    private String role;
}
