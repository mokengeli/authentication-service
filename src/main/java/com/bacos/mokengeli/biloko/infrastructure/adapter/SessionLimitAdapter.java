package com.bacos.mokengeli.biloko.infrastructure.adapter;

import com.bacos.mokengeli.biloko.application.port.SessionLimitPort;
import com.bacos.mokengeli.biloko.infrastructure.model.SessionLimit;
import com.bacos.mokengeli.biloko.infrastructure.repository.SessionLimitRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SessionLimitAdapter implements SessionLimitPort {
    private final SessionLimitRepository sessionLimitRepository;

    public SessionLimitAdapter(SessionLimitRepository sessionLimitRepository) {
        this.sessionLimitRepository = sessionLimitRepository;
    }

    @Override
    public Optional<SessionLimit> findByAppType(String appType) {
        return this.sessionLimitRepository.findByAppType(appType);
    }
}
