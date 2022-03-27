/**
 * 
 */
package com.soccer.management.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soccer.management.dto.AuthenticationRequestDTO;
import com.soccer.management.dto.UserDTO;
import com.soccer.management.service.impl.TeamService;
import com.soccer.management.service.impl.UserService;

/**
 * @author enes.boyaci
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TeamService teamService;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void setup() {
        teamService.deleteAll();
        userService.deleteAll();
        try {
            userService.saveAdminUserIfNotExists();
            userService.save(UserDTO.builder().email("user1@mail.com").password("123456").build());
        } catch (Exception e) {
            // No need implementation
        }
    }

    @Test
    public void givenEmptyEmail_whenAuthenticate_thenReturnBadRequestException() throws JsonProcessingException,
                                                                                 Exception {

        //given - precondition or setup
        AuthenticationRequestDTO authenticationRequestDTO = AuthenticationRequestDTO.builder()
                        .email("").password("123456").build();

        //when - action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequestDTO)))
                        .andDo(MockMvcResultHandlers.print());

        //then - verify the output
        response.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void givenUser_whenAuthenticate_thenReturnSuccess() throws JsonProcessingException,
                                                               Exception {

        //given - precondition or setup
        AuthenticationRequestDTO authenticationRequestDTO = AuthenticationRequestDTO.builder()
                        .email("user1@mail.com").password("123456").build();

        //when - action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequestDTO)))
                        .andDo(MockMvcResultHandlers.print());

        //then - verify the output
        response.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void givenUser_whenRegister_thenReturnSuccess() throws JsonProcessingException,
                                                           Exception {

        //given - precondition or setup 
        AuthenticationRequestDTO authenticationRequestDTO = AuthenticationRequestDTO.builder()
                        .email("user2@mail.com").password("123456").build();

        //when - action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequestDTO)))
                        .andDo(MockMvcResultHandlers.print());

        //then - verify the output
        response.andExpect(MockMvcResultMatchers.status().isCreated());
    }

}
