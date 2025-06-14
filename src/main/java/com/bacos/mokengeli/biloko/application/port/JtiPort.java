package com.bacos.mokengeli.biloko.application.port;

import com.bacos.mokengeli.biloko.infrastructure.model.JwtSession;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface JtiPort {
    UUID createSession(String employeeNumber, String appType, OffsetDateTime expiresAt);

    List<JwtSession> getActiveJti(String employeeNumber, String appType);

    void invalidateSession(UUID jti);
}
