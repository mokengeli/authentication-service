package com.bacos.mokengeli.biloko.infrastructure.repository;

import com.bacos.mokengeli.biloko.infrastructure.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmployeeNumber(String employeeNumber);
    Optional<User> findByUserName(String userName);
    @Query("select u.password from User u where u.employeeNumber = :employeeNumber")
    Optional<String> findPasswordByEmployeeNumber(@Param("employeeNumber") String employeeNumber);

}
