package com.bacos.mokengeli.biloko.presentation.model;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
    private String platformType;
}
