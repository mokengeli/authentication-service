package com.bacos.mokengeli.biloko.application.port;


import com.bacos.mokengeli.biloko.application.domain.model.DomainUser;

import java.util.Optional;

public interface UserPort {
    Optional<DomainUser> findByUserName(String email);
    void createUser(DomainUser user);

}
