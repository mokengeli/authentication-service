package com.bacos.mokengeli.biloko.infrastructure.adapter;

import com.bacos.mokengeli.biloko.application.port.JtiPort;
import com.bacos.mokengeli.biloko.infrastructure.model.JwtSession;
import com.bacos.mokengeli.biloko.infrastructure.repository.JwtSessionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
public class JtiAdapter implements JtiPort {
    private final JwtSessionRepository repo;

    public JtiAdapter(JwtSessionRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional
    public UUID createSession(String employeeNumber, String appType, LocalDateTime expiresAt) {
        repo.deleteByEmployeeNumberAndAppType(employeeNumber, appType);
        UUID jti = UUID.randomUUID();
        repo.save(JwtSession.builder()
                .jti(jti)
                .employeeNumber(employeeNumber)
                .appType(appType)
                .issuedAt(LocalDateTime.now())
                .expiresAt(expiresAt)
                .build()
        );
        return jti;
    }

    @Override
    public Optional<UUID> getActiveJti(String employeeNumber, String appType) {
        return repo.findByEmployeeNumberAndAppType(employeeNumber, appType)
                .filter(s -> s.getExpiresAt().isAfter(LocalDateTime.now()))
                .map(JwtSession::getJti);
    }

    @Override
    @Transactional
    public void invalidateSession(UUID jti) {
        repo.deleteByJti(jti);
    }
}