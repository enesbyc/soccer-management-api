package com.soccer.management.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.soccer.management.consts.Role;
import com.soccer.management.dto.TeamDTO;
import com.soccer.management.dto.UserDTO;
import com.soccer.management.exception.ResourceAlreadyExistsException;
import com.soccer.management.exception.ResourceNotFoundException;
import com.soccer.management.exception.ValidationException;
import com.soccer.management.model.User;
import com.soccer.management.model.mapper.UserMapper;
import com.soccer.management.repository.UserRepository;
import com.soccer.management.security.JwtUtil;
import com.soccer.management.service.IPlayerService;
import com.soccer.management.service.ITeamService;
import com.soccer.management.service.IUserService;

/**
 * @author enes.boyaci
 */
@Service
public class UserService implements UserDetailsService, IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder bcryptEncoder;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ITeamService teamService;

    @Autowired
    private IPlayerService playerService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        List<SimpleGrantedAuthority> roles = null;

        if (Objects.isNull(email) || email.equals(""))
            throw new ValidationException("Email cannot be empty!");

        User user = userRepository.findByEmail(email);
        if (user != null) {
            roles = Arrays.asList(new SimpleGrantedAuthority(user.getRole()));
            return new org.springframework.security.core.userdetails.User(user.getEmail(),
                            user.getPassword(), roles);
        }
        throw new ResourceNotFoundException("User not found with the email " + email);
    }

    @Override
    @Transactional
    public UserDTO save(UserDTO userDTO) {

        boolean isExists = userRepository.existsByEmail(userDTO.getEmail());
        if (isExists)
            throw new ResourceAlreadyExistsException("User already exists with given email!");

        User user = userMapper.toUser(userDTO);
        user.setPassword(bcryptEncoder.encode(userDTO.getPassword()));
        User savedUser = userRepository.save(user);
        UserDTO responseUserDTO = userMapper.toUserDTO(savedUser);
        TeamDTO team = teamService.generateAndSaveTeam(savedUser.getId());
        playerService.generateAndSavePlayer(team.getId());
        teamService.calculateAndUpdatePlayerValue(team.getId());
        return responseUserDTO;

    }

    @Override
    @Transactional
    public User getByEmail(String email) {
        if (Objects.isNull(email) || email.equals(""))
            throw new ValidationException("Email cannot be empty");

        return userRepository.findByEmail(email);

    }

    @Override
    @Transactional
    public List<UserDTO> get() {

        boolean isAdmin = jwtUtil.getIsAdminFromToken();
        List<User> users = new ArrayList<User>();
        if (!isAdmin) {
            User user = userRepository.findByEmail(jwtUtil.getUsernameFromToken());
            users.add(user);
        } else {
            users = userRepository.findAll();
        }

        return userMapper.toUserDTO(users);
    }

    @Override
    @Transactional
    public void saveAdminUserIfNotExists() {

        List<User> userList = userRepository.findByRole(Role.ADMIN.name());

        if (userList.size() == 0) {
            User user = new User("admin@mail.com",
                            "$2a$10$KkKlDigAdKFdqGjEHxx8EesOBx8BqUKHAHCTPhLki7xfMRx.WZOh.",
                            Role.ADMIN.name());
            userRepository.save(user);
        }
    }

    @Override
    @Transactional
    public void deleteByEmail(String email) {
        userRepository.deleteByEmail(email);
    }

    @Override
    @Transactional
    public User getByUserId(long userId) {

        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent())
            return user.get();
        else
            return null;
    }

    @Override
    @Transactional
    public void deleteAll() {
        userRepository.deleteAll();
    }

}
