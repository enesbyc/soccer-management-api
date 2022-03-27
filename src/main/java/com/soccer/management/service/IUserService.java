package com.soccer.management.service;

import java.util.List;

import com.soccer.management.dto.UserDTO;
import com.soccer.management.model.User;

/**
 * @author enes.boyaci
 */
public interface IUserService {
    UserDTO save(UserDTO userDTO);

    User getByEmail(String email);

    List<UserDTO> get();

    void saveAdminUserIfNotExists();

    void deleteByEmail(String email);

    User getByUserId(long userId);

    void deleteAll();
}
