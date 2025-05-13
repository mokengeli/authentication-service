package com.bacos.mokengeli.biloko.application.port;

import com.bacos.mokengeli.biloko.infrastructure.model.JwtSession;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface JtiPort {
    UUID createSession(String employeeNumber, String appType, LocalDateTime expiresAt);

    List<JwtSession> getActiveJti(String employeeNumber, String appType);

    void invalidateSession(UUID jti);
}
