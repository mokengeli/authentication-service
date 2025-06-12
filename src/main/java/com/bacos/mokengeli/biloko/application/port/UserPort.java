package com.bacos.mokengeli.biloko.application.port;


import com.bacos.mokengeli.biloko.application.domain.DomainUser;
import com.bacos.mokengeli.biloko.application.exception.ServiceException;

import java.util.Optional;

public interface UserPort {
    Optional<DomainUser> findByEmployeeNumber(String employeeNumber);

    Optional<DomainUser> createUser(DomainUser user);

    Optional<String> getPassword(String employeeNumber);

    void updatePassword(String employeeNumber, String encode) throws ServiceException;

    Optional<DomainUser> findByUserName(String username);


}
