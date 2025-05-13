package com.bacos.mokengeli.biloko.infrastructure.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSessionDomain {
    private String jti;
    private LocalDateTime issuedAt;
    private LocalDateTime expiresAt;
}