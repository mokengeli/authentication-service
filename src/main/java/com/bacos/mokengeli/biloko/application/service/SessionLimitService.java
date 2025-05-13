package com.bacos.mokengeli.biloko.application.service;

import com.bacos.mokengeli.biloko.application.port.SessionLimitPort;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class SessionLimitService {

    private final SessionLimitPort sessionLimitPort;
    private final Cache<String, Integer> cache;

    @Autowired
    public SessionLimitService(SessionLimitPort sessionLimitPort) {
        this.sessionLimitPort = sessionLimitPort;
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(5))
                .build();
    }

    /**
     * Retourne le quota pour la plate-forme, ou le fallback YAML si absent.
     */
    public int resolve(String appType) {
        return cache.get(appType, key ->
                sessionLimitPort.findByAppType(key)
                        .map(sl -> (int) sl.getMaxSessions())
                        .orElse(1));
    }
}
