package com.peter.tanxuanfood.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("userDetailsService")
@RequiredArgsConstructor
public class UserDetailsCustom implements UserDetailsService {

    private final UserService userService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.peter.tanxuanfood.domain.User user = this.userService.handleGetUserByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username/Password not found");
        }

        // get Roles
        List<SimpleGrantedAuthority> grantedAuthorities = user
                .getRoles()
                .stream()
                .map(role -> role
                        .getName()
                        .name())
                .map(SimpleGrantedAuthority::new)
                .toList();
        return new User(user.getEmail(), user.getPassword(), grantedAuthorities);
    }
}
