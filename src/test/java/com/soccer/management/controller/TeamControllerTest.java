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
import com.soccer.management.consts.SoccerConst;
import com.soccer.management.dto.AuthenticationRequestDTO;
import com.soccer.management.dto.AuthenticationResponseDTO;
import com.soccer.management.dto.PlayerDTO;
import com.soccer.management.dto.TeamDTO;
import com.soccer.management.dto.UserDTO;
import com.soccer.management.model.Team;
import com.soccer.management.model.mapper.TeamMapper;
import com.soccer.management.repository.TeamRepository;
import com.soccer.management.security.JwtUtil;
import com.soccer.management.service.impl.TeamService;
import com.soccer.management.service.impl.UserService;

/**
 * @author enes.boyaci
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class TeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamMapper teamMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private PlayerDTO playerDTO;

    private String token;

    private TeamDTO teamDTO;

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

        teamDTO = TeamDTO.builder().id(1L).name("Enes Team").balance(SoccerConst.teamInitialBalance)
                        .country("Turkey").playerValue(20 * SoccerConst.singlePlayerInitialBalance)
                        .build();
        initializeToken();
    }

    @Test
    public void givenTeamWithoutToken_whenCreate_thenReturnUnauthorized() throws JsonProcessingException,
                                                                          Exception {

        //when - action or the behaviour that we are going test
        ResultActions response = mockMvc
                        .perform(MockMvcRequestBuilders.post("/team")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(playerDTO)))
                        .andDo(MockMvcResultHandlers.print());

        //then - verify the output
        response.andExpect(MockMvcResultMatchers.status().isUnauthorized())
                        .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void givenTeam_whenCreate_thenReturnCreatedTeam() throws JsonProcessingException,
                                                             Exception {

        Team teamFromDb = teamRepository.findByEmail(jwtUtil.getUsernameFromToken(token));
        if (Objects.nonNull(teamFromDb))
            teamService.delete(teamFromDb.getId());

        //when - action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/team")
                        .contentType(MediaType.APPLICATION_JSON).with(addTokenToHeader())
                        .content(objectMapper.writeValueAsString(teamDTO)))
                        .andDo(MockMvcResultHandlers.print());

        //then - verify the output
        response.andExpect(MockMvcResultMatchers.status().isCreated())
                        .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void givenNonExistsTeamId_whenDelete_thenReturnBadRequest() throws JsonProcessingException,
                                                                       Exception {

        //when - action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.delete("/team/" + 87979797)
                        .contentType(MediaType.APPLICATION_JSON).with(addTokenToHeader()))
                        .andDo(MockMvcResultHandlers.print());

        //then - verify the output
        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                        .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void givenExistsTeamId_whenDelete_thenReturnSuccess() throws JsonProcessingException,
                                                                 Exception {

        Team team = teamRepository.findTopByOrderByIdDesc();

        //when - action or the behaviour that we are going test
        ResultActions response = mockMvc
                        .perform(MockMvcRequestBuilders.delete("/team/" + team.getId())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .with(addTokenToHeader()))
                        .andDo(MockMvcResultHandlers.print());

        //then - verify the output
        response.andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void given_whenGet_thenReturnTeamList() throws JsonProcessingException, Exception {

        //when - action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/team")
                        .contentType(MediaType.APPLICATION_JSON).with(addTokenToHeader()))
                        .andDo(MockMvcResultHandlers.print());

        //then - verify the output
        response.andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void givenTeam_whenUpdate_thenReturnUpdatedTeam() throws JsonProcessingException,
                                                             Exception {

        TeamDTO teamObj = teamMapper.toTeamDTO(teamRepository.findTopByOrderByIdDesc());
        teamObj.setName("Ekrem Team");
        //when - action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.patch("/team")
                        .contentType(MediaType.APPLICATION_JSON).with(addTokenToHeader())
                        .content(objectMapper.writeValueAsString(teamObj)))
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
