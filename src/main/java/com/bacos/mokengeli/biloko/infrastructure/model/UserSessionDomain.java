package com.bacos.mokengeli.biloko.infrastructure.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSessionDomain {
    private String jti;
    private OffsetDateTime issuedAt;
    private OffsetDateTime expiresAt;
}