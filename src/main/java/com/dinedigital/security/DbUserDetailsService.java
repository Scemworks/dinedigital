package com.dinedigital.security;

import com.dinedigital.dao.UserDao;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DbUserDetailsService implements UserDetailsService {
    private final UserDao userDao;

    public DbUserDetailsService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var opt = userDao.findByUsername(username);
        var u = opt.orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<GrantedAuthority> auths = List.of(new SimpleGrantedAuthority("ROLE_" + u.getRole()));
        return new User(u.getUsername(), u.getPasswordHash(), auths);
    }
}
