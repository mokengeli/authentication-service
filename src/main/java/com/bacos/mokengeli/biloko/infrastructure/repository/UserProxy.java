package com.bacos.mokengeli.biloko.infrastructure.repository;


import com.bacos.mokengeli.biloko.application.domain.model.DomainUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;


@FeignClient(name = "user-service",
        configuration = com.bacos.mokengeli.biloko.config.feign.FeignClientConfig.class)
public interface UserProxy {
    @PostMapping("/api/user")
    Optional<DomainUser> createUser(@RequestBody DomainUser userRequest);
}