package com.bacos.mokengeli.biloko.application.port;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface JtiPort {
    UUID createSession(String employeeNumber, String appType, LocalDateTime expiresAt);

    Optional<UUID> getActiveJti(String employeeNumber, String appType);

    void invalidateSession(UUID jti);
}
