package com.bacos.mokengeli.biloko.infrastructure.repository;

import com.bacos.mokengeli.biloko.infrastructure.model.SessionLimit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionLimitRepository extends JpaRepository<SessionLimit, Long> {

    Optional<SessionLimit> findByAppType(String appType);
}
