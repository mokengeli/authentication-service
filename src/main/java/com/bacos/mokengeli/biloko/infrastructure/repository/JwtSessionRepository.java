package com.bacos.mokengeli.biloko.infrastructure.repository;

import com.bacos.mokengeli.biloko.infrastructure.model.JwtSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface JwtSessionRepository extends JpaRepository<JwtSession, UUID> {
    Optional<JwtSession> findByEmployeeNumberAndAppType(String employeeNumber, String appType);

    void deleteByEmployeeNumberAndAppType(String employeeNumber, String appType);

    @Modifying
    @Query("DELETE FROM JwtSession js WHERE js.jti = :jti")
    void deleteByJti(@Param("jti") UUID jti);
}