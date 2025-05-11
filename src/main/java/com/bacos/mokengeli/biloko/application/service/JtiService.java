package com.bacos.mokengeli.biloko.application.service;

import com.bacos.mokengeli.biloko.application.port.JtiPort;
import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class JtiService {
    private final JtiPort jtiPort;

    public JtiService(JtiPort jtiPort) {
        this.jtiPort = jtiPort;
    }

    public UUID createSession(String employeeNumber, String appType, LocalDateTime expiresAt) {
        return jtiPort.createSession(employeeNumber, appType, expiresAt);
    }

    public Optional<UUID> getActiveJti(String employeeNumber, String appType) {
        return jtiPort.getActiveJti(employeeNumber, appType);
    }

    @Async
    @Retryable(
            value = {DataAccessException.class, OptimisticLockException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public void invalidateSession(UUID jti) {
        jtiPort.invalidateSession(jti);
    }

    /**
     * Méthode de secours si les retries ont tous échoué.
     */
    @Recover
    public void recoverInvalidateSession(DataAccessException ex, UUID jti) {
        // loggez, alertez, ou stockez l’échec pour un traitement manuel ultérieur
        log.error("Échec répété de l’invalidation JTI=" + jti, ex);
    }
}
