package com.bacos.mokengeli.biloko.infrastructure.adapter;

import com.bacos.mokengeli.biloko.application.port.JtiPort;
import com.bacos.mokengeli.biloko.application.service.SessionLimitService;
import com.bacos.mokengeli.biloko.infrastructure.model.JwtSession;
import com.bacos.mokengeli.biloko.infrastructure.repository.JwtSessionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Component
public class JtiAdapter implements JtiPort {
    private final JwtSessionRepository repo;
    private final SessionLimitService limitService;

    @Autowired
    public JtiAdapter(JwtSessionRepository repo, SessionLimitService limitService) {
        this.repo = repo;
        this.limitService = limitService;
    }

    @Override
    @Transactional
    public UUID createSession(String employeeNumber,
                              String appType,
                              OffsetDateTime expiresAt) {

        /* 1. Récupère toutes les sessions de l’utilisateur / plate-forme */
        List<JwtSession> sessions = repo
                .findByEmployeeNumberAndAppType(employeeNumber, appType)
                .orElse(List.of());

        /* 2. Supprime celles déjà expirées */
        OffsetDateTime now = OffsetDateTime.now();
        sessions.stream()
                .filter(s -> s.getExpiresAt().isBefore(now))
                .forEach(repo::delete);

        /* 3. Applique le quota */
        int max = limitService.resolve(appType);

        List<JwtSession> active = sessions.stream()
                .filter(s -> s.getExpiresAt().isAfter(now))
                .sorted(Comparator.comparing(JwtSession::getIssuedAt))     // plus vieilles d’abord
                .toList();

        if (active.size() >= max) {
            // on supprime juste le(s) plus ancien(s) pour faire de la place
            int toRemove = active.size() - max + 1;   // +1 pour accueillir la future session
            active.stream()
                    .limit(toRemove)
                    .forEach(repo::delete);
        }

        /* 4. Enregistre la nouvelle session */
        UUID newJti = UUID.randomUUID();
        repo.save(JwtSession.builder()
                .jti(newJti)
                .employeeNumber(employeeNumber)
                .appType(appType)
                .issuedAt(now)
                .expiresAt(expiresAt)
                .build());

        return newJti;
    }


    @Override
    public List<JwtSession> getActiveJti(String employeeNumber, String appType) {
        return repo.findByEmployeeNumberAndAppType(employeeNumber, appType) // Optional<List<JwtSession>>
                .orElse(List.of())                                        // si absent → liste vide
                .stream()
                .filter(session -> session.getExpiresAt().isAfter(OffsetDateTime.now()))
                .toList();                                                // Java 17+ ; sinon collect(Collectors.toList())
    }


    @Override
    @Transactional
    public void invalidateSession(UUID jti) {
        repo.deleteByJti(jti);
    }
}