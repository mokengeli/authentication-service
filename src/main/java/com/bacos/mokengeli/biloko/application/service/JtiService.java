package com.bacos.mokengeli.biloko.application.service;

import com.bacos.mokengeli.biloko.application.port.JtiPort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

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

    public void invalidateSession(UUID jti) {
        jtiPort.invalidateSession(jti);
    }
}
