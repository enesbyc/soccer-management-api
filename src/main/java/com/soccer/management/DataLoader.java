package com.soccer.management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.soccer.management.dto.UserDTO;
import com.soccer.management.service.impl.UserService;

@Component
public class DataLoader implements ApplicationRunner {

    @Autowired
    private UserService userService;

    public void run(ApplicationArguments args) {
        try {
            userService.saveAdminUserIfNotExists();
            userService.save(UserDTO.builder().email("user1@mail.com").password("123456").build());
        } catch (Exception e) {
            // No need implementation
        }

    }
}