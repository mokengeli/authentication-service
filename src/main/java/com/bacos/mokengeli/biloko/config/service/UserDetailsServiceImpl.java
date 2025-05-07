package com.bacos.mokengeli.biloko.config.service;

import com.bacos.mokengeli.biloko.application.domain.DomainUser;

import com.bacos.mokengeli.biloko.application.service.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private AuthenticationService authenticationService;



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Entering in loadUserByUsername Method...");
        Optional<DomainUser> optUser = authenticationService.findUserByEmployeeNumber(username);
        if (optUser.isEmpty()) {
            log.error("Username not found: " + username);
            throw new UsernameNotFoundException("could not found user..!!");
        }
        log.info("loadUserByUsername username {}..!!!",username);
        return new CustomUserInfoDetails(optUser.get());

    }

}
