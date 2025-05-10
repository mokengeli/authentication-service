package com.bacos.mokengeli.biloko.infrastructure.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "jwt_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtSession {
    @Id
    private UUID jti;
    @Column(nullable = false)
    private String employeeNumber;
    @Column(nullable = false)
    private String appType;
    @Column(nullable = false)
    private LocalDateTime issuedAt;
    @Column(nullable = false)
    private LocalDateTime expiresAt;
}