package com.bacos.mokengeli.biloko.application.port;


import com.bacos.mokengeli.biloko.application.domain.model.DomainUser;

import java.util.Optional;

public interface UserPort {
    Optional<DomainUser> findByEmployeeNumber(String email);
    Optional<DomainUser> createUser(DomainUser user);

}
