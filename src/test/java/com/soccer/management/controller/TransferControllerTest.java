/**
 * 
 */
package com.soccer.management.controller;

import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soccer.management.dto.AuthenticationRequestDTO;
import com.soccer.management.dto.AuthenticationResponseDTO;
import com.soccer.management.dto.PlayerDTO;
import com.soccer.management.dto.TransferDTO;
import com.soccer.management.dto.UserDTO;
import com.soccer.management.service.impl.TeamService;
import com.soccer.management.service.impl.UserService;

/**
 * @author enes.boyaci
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private TeamService teamService;

    private String token;

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
        initializeToken();
    }

    @Test
    public void given_whenGet_thenReturnTransfer() throws JsonProcessingException, Exception {

        //given - precondition or setup

        //when - action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/transfer")
                        .contentType(MediaType.APPLICATION_JSON).with(addTokenToHeader()))
                        .andDo(MockMvcResultHandlers.print());

        //then - verify the output
        response.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void givenTransfer_whenGet_thenReturnTransfer() throws JsonProcessingException,
                                                           Exception {

        //given - precondition or setup

        //when - action or the behaviour that we are going test
        PlayerDTO player = getPlayers()[0];
        TransferDTO transferDTO = TransferDTO.builder().playerId(player.getId())
                        .transferAmount(new Double(100000)).build();
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON).with(addTokenToHeader())
                        .content(objectMapper.writeValueAsString(transferDTO)))
                        .andDo(MockMvcResultHandlers.print());

        //then - verify the output
        response.andExpect(MockMvcResultMatchers.status().isCreated());

    }

    @Test
    public void givenNonExistsTransfer_whenDelete_thenReturnBadRequest() throws JsonProcessingException,
                                                                         Exception {

        //given - precondition or setup

        //when - action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.delete("/transfer/" + 999)
                        .contentType(MediaType.APPLICATION_JSON).with(addTokenToHeader()))
                        .andDo(MockMvcResultHandlers.print());

        //then - verify the output
        response.andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    public PlayerDTO[] getPlayers() {
        PlayerDTO[] playerArr = null;
        try {
            ResultActions responsePlayerList = mockMvc.perform(MockMvcRequestBuilders.get("/player")
                            .contentType(MediaType.APPLICATION_JSON).with(addTokenToHeader()))
                            .andDo(MockMvcResultHandlers.print());

            MvcResult resulPlayerList = responsePlayerList.andReturn();
            playerArr = objectMapper.readValue(resulPlayerList.getResponse().getContentAsString(),
                                               PlayerDTO[].class);
        } catch (Exception e) {}

        return playerArr;
    }

    public void initializeToken() {
        try {
            AuthenticationRequestDTO authenticationRequestDTO = AuthenticationRequestDTO.builder()
                            .email("user1@mail.com").password("123456").build();
            ResultActions responseAuthentication = mockMvc.perform(MockMvcRequestBuilders
                            .post("/authenticate").contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(authenticationRequestDTO)))
                            .andDo(MockMvcResultHandlers.print());
            MvcResult resultToken = responseAuthentication.andReturn();
            AuthenticationResponseDTO authenticationResponse = objectMapper
                            .readValue(resultToken.getResponse().getContentAsString(),
                                       AuthenticationResponseDTO.class);
            token = authenticationResponse.getToken();
        } catch (Exception e) {}

    }

    public RequestPostProcessor addTokenToHeader() {
        return request -> {
            if (Objects.nonNull(token) && !token.equals(""))
                request.addHeader("Authorization", "Bearer " + token);
            return request;
        };
    }

}
