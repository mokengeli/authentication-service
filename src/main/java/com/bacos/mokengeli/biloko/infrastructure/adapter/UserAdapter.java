package com.bacos.mokengeli.biloko.infrastructure.adapter;

import com.bacos.mokengeli.biloko.application.domain.DomainUser;
import com.bacos.mokengeli.biloko.application.exception.ServiceException;
import com.bacos.mokengeli.biloko.application.port.UserPort;
import com.bacos.mokengeli.biloko.infrastructure.mapper.UserMapper;
import com.bacos.mokengeli.biloko.infrastructure.model.User;
import com.bacos.mokengeli.biloko.infrastructure.repository.UserRepository;
import com.bacos.mokengeli.biloko.infrastructure.repository.proxy.UserProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@Repository
public class UserAdapter implements UserPort {
    private final UserProxy userProxy;
    private final UserRepository userRepository;

    @Autowired
    public UserAdapter(UserProxy userProxy, UserRepository userRepository) {
        this.userProxy = userProxy;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<DomainUser> findByEmployeeNumber(String employeeNumber) {
        Optional<User> optUser = this.userRepository.findByEmployeeNumber(employeeNumber);
        if (optUser.isEmpty()) {
            return Optional.empty();
        }
        User user = optUser.get();
        return Optional.of(UserMapper.toDomainWithPwd(user));
    }

    @Override
    public Optional<DomainUser> createUser(DomainUser user) {
        return userProxy.createUser(user);
    }

    @Override
    public Optional<String> getPassword(String employeeNumber) {
        return this.userRepository.findPasswordByEmployeeNumber(employeeNumber);
    }

    @Override
    public void updatePassword(String employeeNumber, String encodedPwd) throws ServiceException {
        User user = userRepository.findByEmployeeNumber(employeeNumber)
                .orElseThrow(() -> new ServiceException(
                        UUID.randomUUID().toString(), "Utilisateur inexistant"));
        user.setPassword(encodedPwd);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<DomainUser> findByUserName(String username) {
        Optional<User> optUser = this.userRepository.findByUserName(username);
        if (optUser.isEmpty()) {
            return Optional.empty();
        }
        User user = optUser.get();
        return Optional.of(UserMapper.toDomainWithPwd(user));
    }
}
