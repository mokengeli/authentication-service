package com.bacos.mokengeli.biloko.presentation.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AuthResponseDto {
    private Long id;
    private String username;
    private String email;
    private List<String> roles;
    private List<String> permissions;
}
