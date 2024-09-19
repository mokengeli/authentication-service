package com.bacos.mokengeli.biloko.application.domain.service;


import com.bacos.mokengeli.biloko.application.domain.model.UserInfo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomUserInfoDetails extends UserInfo implements UserDetails {

    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserInfoDetails(UserInfo user) {
        setId(user.getId());
        setEmail(user.getEmail());
        this.username = user.getEmployeeNumber();
        this.password = user.getPassword();
        setRoles(user.getRoles());
        List<GrantedAuthority> auths = new ArrayList<>();

        for (String permission : user.getPermissions()) {
            auths.add(new SimpleGrantedAuthority(permission.toUpperCase()));
        }
        this.authorities = auths;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}