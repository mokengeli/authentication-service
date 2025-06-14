package com.bacos.mokengeli.biloko.application.service;

import com.bacos.mokengeli.biloko.application.port.JtiPort;
import com.bacos.mokengeli.biloko.infrastructure.model.JwtSession;
import com.bacos.mokengeli.biloko.infrastructure.model.UserSessionDomain;
import com.bacos.mokengeli.biloko.infrastructure.model.UserSessionListDomain;
import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class JtiService {
    private final JtiPort jtiPort;
    private final SessionLimitService sessionLimitService;

    @Autowired
    public JtiService(JtiPort jtiPort, SessionLimitService sessionLimitService) {
        this.jtiPort = jtiPort;
        this.sessionLimitService = sessionLimitService;
    }

    public UUID createSession(String employeeNumber, String appType, OffsetDateTime expiresAt) {
        return jtiPort.createSession(employeeNumber, appType, expiresAt);
    }

    public UserSessionListDomain getActiveJti(String employeeNumber, String appType) {

        List<JwtSession> sessions = jtiPort.getActiveJti(employeeNumber, appType)
                .stream()
                .filter(s -> s.getExpiresAt().isAfter(OffsetDateTime.now()))
                .sorted(Comparator.comparing(JwtSession::getIssuedAt)
                        .reversed())       // plus récentes d’abord
                .toList();
        int max = sessionLimitService.resolve(appType);

        // Tronque si nécessaire
        List<UserSessionDomain> allowed = sessions.stream()
                .limit(max)
                .map(s -> new UserSessionDomain(s.getJti().toString(), s.getIssuedAt(), s.getExpiresAt()))
                .toList();

        return new UserSessionListDomain(employeeNumber, appType, max, allowed);
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
