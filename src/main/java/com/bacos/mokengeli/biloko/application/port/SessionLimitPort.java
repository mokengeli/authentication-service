package com.bacos.mokengeli.biloko.application.port;

import com.bacos.mokengeli.biloko.infrastructure.model.SessionLimit;

import java.util.Optional;

public interface SessionLimitPort {

    Optional<SessionLimit> findByAppType(String appType);
}
