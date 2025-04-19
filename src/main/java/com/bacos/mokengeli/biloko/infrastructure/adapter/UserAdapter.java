package com.bacos.mokengeli.biloko.infrastructure.adapter;

import com.bacos.mokengeli.biloko.application.domain.model.DomainUser;
import com.bacos.mokengeli.biloko.application.port.UserPort;
import com.bacos.mokengeli.biloko.infrastructure.mapper.UserMapper;
import com.bacos.mokengeli.biloko.infrastructure.model.User;
import com.bacos.mokengeli.biloko.infrastructure.repository.UserProxy;
import com.bacos.mokengeli.biloko.infrastructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public class UserAdapter implements UserPort {
    private final UserProxy userProxy;
    private final UserRepository userRepository;

    @Autowired
    public UserAdapter(UserProxy userProxy, UserRepository userRepository) {
        this.userProxy = userProxy;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<DomainUser> findByEmployeeNumber(String employeeNumber) {
        Optional<User> optUser = this.userRepository.findByEmployeeNumber(employeeNumber);
        if (optUser.isEmpty()) {
            return Optional.empty();
        }
        User user = optUser.get();
        return Optional.of(UserMapper.toDomain(user));
    }

    @Override
    public Optional<DomainUser> createUser(DomainUser user) {
        return userProxy.createUser(user);
    }
}
