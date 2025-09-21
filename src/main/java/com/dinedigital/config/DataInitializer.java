package com.dinedigital.config;

import com.dinedigital.dao.UserDao;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initUsers(UserDao userDao, PasswordEncoder encoder) {
        return args -> {
            if (userDao.countAdmins() == 0) {
                userDao.insert("admin", encoder.encode("admin123"), "ADMIN");
            }
        };
    }
}
