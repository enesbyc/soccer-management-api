package com.soccer.management.service.impl.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.soccer.management.consts.SoccerConst;
import com.soccer.management.dto.TeamDTO;
import com.soccer.management.dto.UserDTO;
import com.soccer.management.exception.ResourceAlreadyExistsException;
import com.soccer.management.exception.ResourceNotFoundException;
import com.soccer.management.exception.ValidationException;
import com.soccer.management.model.User;
import com.soccer.management.model.mapper.UserMapper;
import com.soccer.management.repository.UserRepository;
import com.soccer.management.security.JwtUtil;
import com.soccer.management.service.impl.PlayerService;
import com.soccer.management.service.impl.TeamService;
import com.soccer.management.service.impl.UserService;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder bcryptEncoder;

    @InjectMocks
    private UserService userService;

    @Mock
    private TeamService teamService;

    @Mock
    private PlayerService playerService;

    @Mock
    private JwtUtil jwtUtil;

    private User user;
    private UserDTO userDTO;
    private TeamDTO teamDTO;

    @BeforeEach
    public void setup() {
        user = User.builder().id(1L).email("enesbyc19@gmail.com").password("123456").build();
        userDTO = UserDTO.builder().email("enesbyc19@gmail.com").password("123456").build();
        teamDTO = TeamDTO.builder().id(1L).name("Enes Team").balance(SoccerConst.teamInitialBalance)
                        .country("Turkey").playerValue(20 * SoccerConst.singlePlayerInitialBalance)
                        .build();
    }

    @Test
    @DisplayName("JUnit test for load user by username with given empty email")
    public void givenEmptyEmail_whenloadUserByUsername_thenReturnValidationException() {

        //given - precondition or setup

        //when - action or the behaviour that we are going test

        org.junit.jupiter.api.Assertions.assertThrows(ValidationException.class, () -> {
            userService.loadUserByUsername(null);
        });

        //then - verify the output
        verify(userRepository, never()).findByEmail(any());

    }

    @Test
    @DisplayName("JUnit test for load user by username with given non exists email")
    public void givenNonExistsEmail_whenloadUserByUsername_thenReturnResourceNotFoundException() {

        //given - precondition or setup

        //when - action or the behaviour that we are going test

        org.junit.jupiter.api.Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            userService.loadUserByUsername("enesbyc19@mail.com");
        });

        //then - verify the output

    }

    @Test
    @DisplayName("JUnit test for save user with given exists email")
    public void givenExistsEmail_whenSaveUser_thenResourceAlreadyExistsException() {

        //given - precondition or setup
        given(userRepository.existsByEmail(user.getEmail())).willReturn(true);

        //when - action or the behaviour that we are going test
        org.junit.jupiter.api.Assertions.assertThrows(ResourceAlreadyExistsException.class, () -> {
            userService.save(userDTO);
        });

        //then - verify the output
        verify(userMapper, never()).toUser(userDTO);
        verify(userRepository, never()).save(user);

    }

    @Test
    @DisplayName("JUnit test for save user with given exists email")
    public void givenUser_whenSaveUser_thenReturnUser() {

        //given - precondition or setup
        given(userRepository.existsByEmail(user.getEmail())).willReturn(false);
        given(userMapper.toUser(userDTO)).willReturn(user);
        given(bcryptEncoder.encode(userDTO.getPassword()))
                        .willReturn("$2a$10$FDPHHEGwcyWcXQMREEQyXuUmAajm9RfkwJxsN1tcQQoXEHFJ5CnjG");
        given(userRepository.save(user)).willReturn(user);
        given(userMapper.toUserDTO(user)).willReturn(userDTO);
        given(teamService.generateAndSaveTeam(user.getId())).willReturn(teamDTO);

        //when - action or the behaviour that we are going test
        UserDTO savedUser = userService.save(userDTO);

        //then - verify the output
        assertThat(savedUser).isNotNull();

    }

    @Test
    @DisplayName("JUnit test for get by email ")
    public void givenExistsEmail_whenGetByEmail_thenReturnUser() {

        //given - precondition or setup
        given(userRepository.findByEmail(user.getEmail())).willReturn(user);

        //when - action or the behaviour that we are going test
        User returnedUser = userService.getByEmail(user.getEmail());

        //then - verify the output
        assertThat(returnedUser).isNotNull();

    }

    @Test
    @DisplayName("JUnit test for get by email with empty email")
    public void givenEmptyEmail_whenGetByEmail_thenValidationException() {

        //given - precondition or setup

        //when - action or the behaviour that we are going test
        org.junit.jupiter.api.Assertions.assertThrows(ValidationException.class, () -> {
            userService.getByEmail("");
        });

        //then - verify the output
        verify(userRepository, never()).findByEmail("");

    }

    @Test
    @DisplayName("JUnit test for get user with user token")
    public void givenIsUserToken_whenGet_thenUsers() {

        //given - precondition or setup
        List<User> userList = new ArrayList<User>();
        userList.add(user);
        List<UserDTO> userDTOList = new ArrayList<UserDTO>();
        userDTOList.add(userDTO);
        given(jwtUtil.getIsAdminFromToken()).willReturn(false);
        given(jwtUtil.getUsernameFromToken()).willReturn(user.getEmail());
        given(userRepository.findByEmail(user.getEmail())).willReturn(user);
        given(userMapper.toUserDTO(userList)).willReturn(userDTOList);

        //when - action or the behaviour that we are going test
        List<UserDTO> returnedUserList = userService.get();

        //then - verify the output
        assertThat(returnedUserList).isNotEmpty();
        assertThat(returnedUserList.size()).isEqualTo(1);

    }

    @Test
    @DisplayName("JUnit test for get user with admin token")
    public void givenIsAdminToken_whenGet_thenUsers() {

        //given - precondition or setup
        List<User> userList = new ArrayList<User>();
        userList.add(user);
        List<UserDTO> userDTOList = new ArrayList<UserDTO>();
        userDTOList.add(userDTO);
        given(jwtUtil.getIsAdminFromToken()).willReturn(true);
        given(userRepository.findAll()).willReturn(userList);
        given(userMapper.toUserDTO(userList)).willReturn(userDTOList);

        //when - action or the behaviour that we are going test
        List<UserDTO> returnedUserList = userService.get();

        //then - verify the output
        assertThat(returnedUserList).isNotEmpty();
        assertThat(returnedUserList.size()).isEqualTo(1);

    }

}
