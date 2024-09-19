package com.bacos.mokengeli.biloko.infrastructure.adapter;

import com.bacos.mokengeli.biloko.application.domain.model.UserInfo;
import com.bacos.mokengeli.biloko.application.port.UserPort;
import com.bacos.mokengeli.biloko.infrastructure.repository.UserProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public class UserAdapter implements UserPort {
    private final UserProxy userProxy;

    @Autowired
    public UserAdapter(UserProxy userProxy) {
        this.userProxy = userProxy;
    }

    @Override
    public Optional<UserInfo> findByUserName(String email) {
        UserInfo userResponse = userProxy.findByUserName(email);
        return Optional.ofNullable(userResponse);
    }

    @Override
    public void createUser(UserInfo user) {
         userProxy.createUser(user);
    }
}
