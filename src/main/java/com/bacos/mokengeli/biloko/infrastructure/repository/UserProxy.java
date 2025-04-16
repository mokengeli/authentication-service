package com.bacos.mokengeli.biloko.infrastructure.repository;


import com.bacos.mokengeli.biloko.application.domain.model.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "user-service")
public interface UserProxy {

    @GetMapping("/api/user/by-username")
    UserInfo findByUserName(@RequestParam("username") String username);

    @PostMapping("/api/user")
    void createUser(@RequestBody UserInfo userRequest);
}