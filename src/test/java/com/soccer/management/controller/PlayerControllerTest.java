/**
 * 
 */
package com.soccer.management.controller;

import java.util.Objects;

import org.hamcrest.CoreMatchers;
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
import com.soccer.management.consts.PlayerType;
import com.soccer.management.consts.SoccerConst;
import com.soccer.management.dto.AuthenticationRequestDTO;
import com.soccer.management.dto.AuthenticationResponseDTO;
import com.soccer.management.dto.PlayerDTO;
import com.soccer.management.dto.UserDTO;
import com.soccer.management.model.Player;
import com.soccer.management.model.mapper.PlayerMapper;
import com.soccer.management.repository.PlayerRepository;
import com.soccer.management.service.impl.TeamService;
import com.soccer.management.service.impl.UserService;

/**
 * @author enes.boyaci
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class PlayerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerMapper playerMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private TeamService teamService;

    private Player player;
    private PlayerDTO playerDTO;

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

        playerDTO = PlayerDTO.builder().age(20).country(SoccerConst.defaultCountry)
                        .firstName("Enes").lastName("Boyaci").type(PlayerType.ATTACKER.getType())
                        .marketValue(SoccerConst.singlePlayerInitialBalance).teamId(1L).build();
        player = Player.builder().age(20).country(SoccerConst.defaultCountry).firstName("Enes")
                        .lastName("Boyaci").type(PlayerType.ATTACKER.getType())
                        .marketValue(SoccerConst.singlePlayerInitialBalance).teamId(1L).build();
        initializeToken();
    }

    @Test
    public void givenPlayerWithoutToken_whenCreate_thenReturnUnauthorized() throws JsonProcessingException,
                                                                            Exception {

        //when - action or the behaviour that we are going test
        ResultActions response = mockMvc
                        .perform(MockMvcRequestBuilders.post("/player")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(playerDTO)))
                        .andDo(MockMvcResultHandlers.print());

        //then - verify the output
        response.andExpect(MockMvcResultMatchers.status().isUnauthorized())
                        .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void givenPlayer_whenCreate_thenReturnSavedPlayer() throws JsonProcessingException,
                                                               Exception {

        //when - action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/player")
                        .contentType(MediaType.APPLICATION_JSON).with(addTokenToHeader())
                        .content(objectMapper.writeValueAsString(playerDTO)))
                        .andDo(MockMvcResultHandlers.print());

        //then - verify the output
        response.andExpect(MockMvcResultMatchers.status().isCreated())
                        .andExpect(MockMvcResultMatchers
                                        .jsonPath("$.firstName",
                                                  CoreMatchers.is(player.getFirstName())))
                        .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void givenPlayerWithoutName_whenCreate_thenReturnBadRequest() throws JsonProcessingException,
                                                                         Exception {

        playerDTO.setFirstName("");

        //when - action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/player")
                        .contentType(MediaType.APPLICATION_JSON).with(addTokenToHeader())
                        .content(objectMapper.writeValueAsString(playerDTO)))
                        .andDo(MockMvcResultHandlers.print());

        //then - verify the output
        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                        .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void givenNonExistsPlayerId_whenDelete_thenReturnBadRequest() throws JsonProcessingException,
                                                                         Exception {

        //when - action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.delete("/player/" + 5000)
                        .contentType(MediaType.APPLICATION_JSON).with(addTokenToHeader()))
                        .andDo(MockMvcResultHandlers.print());

        //then - verify the output
        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                        .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void given_whenGet_thenReturnPlayerList() throws JsonProcessingException, Exception {

        //when - action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/player")
                        .contentType(MediaType.APPLICATION_JSON).with(addTokenToHeader()))
                        .andDo(MockMvcResultHandlers.print());

        //then - verify the output
        response.andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void givenPlayer_whenUpdate_thenReturnUpdatedPlayer() throws JsonProcessingException,
                                                                 Exception {

        PlayerDTO playerObj = playerMapper.toPlayerDTO(playerRepository.findTopByOrderByIdDesc());
        playerObj.setFirstName("Ekrem");
        //when - action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.patch("/player")
                        .contentType(MediaType.APPLICATION_JSON).with(addTokenToHeader())
                        .content(objectMapper.writeValueAsString(playerObj)))
                        .andDo(MockMvcResultHandlers.print());

        //then - verify the output
        response.andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print());
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
