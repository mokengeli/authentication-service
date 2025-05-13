package com.bacos.mokengeli.biloko.infrastructure.repository.proxy;


import com.bacos.mokengeli.biloko.application.domain.DomainUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;


@FeignClient(name = "${user.service-id}",
        configuration = com.bacos.mokengeli.biloko.config.feign.FeignClientConfig.class)
public interface UserProxy {
    @PostMapping("/api/user")
    Optional<DomainUser> createUser(@RequestBody DomainUser userRequest);
}