package com.bacos.mokengeli.biloko.application.port;


import com.bacos.mokengeli.biloko.application.domain.model.UserInfo;

import java.util.Optional;

public interface UserPort {
    Optional<UserInfo> findByUserName(String email);
    void createUser(UserInfo user);

}
