package com.soccer.management.service.impl.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.soccer.management.consts.SoccerConst;
import com.soccer.management.dto.PlayerDTO;
import com.soccer.management.dto.TeamDTO;
import com.soccer.management.dto.UserDTO;
import com.soccer.management.exception.BadRequestException;
import com.soccer.management.exception.ResourceNotFoundException;
import com.soccer.management.model.Player;
import com.soccer.management.model.Team;
import com.soccer.management.model.User;
import com.soccer.management.model.mapper.PlayerMapper;
import com.soccer.management.repository.PlayerRepository;
import com.soccer.management.security.JwtUtil;
import com.soccer.management.service.impl.PlayerService;
import com.soccer.management.service.impl.TeamService;
import com.soccer.management.service.impl.UserService;
import com.soccer.management.util.PlayerUtil;

@ExtendWith(MockitoExtension.class)
public class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerService playerService;

    @Mock
    private PlayerMapper playerMapper;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserService userService;

    @Mock
    private TeamService teamService;

    private Player player;
    private PlayerDTO playerDTO;
    private User user;
    private UserDTO userDTO;
    private TeamDTO teamDTO;
    private Team team;

    @BeforeEach
    public void setup() {
        team = Team.builder().id(1L).name("Enes Team").balance(SoccerConst.teamInitialBalance)
                        .country("Turkey").playerValue(20 * SoccerConst.singlePlayerInitialBalance)
                        .build();
        player = Player.builder().age(20).country(SoccerConst.defaultCountry).firstName("Enes")
                        .lastName("Boyaci").marketValue(SoccerConst.singlePlayerInitialBalance)
                        .teamId(team.getId()).team(team).build();
        playerDTO = PlayerDTO.builder().age(20).country(SoccerConst.defaultCountry)
                        .firstName("Enes").lastName("Boyaci")
                        .marketValue(SoccerConst.singlePlayerInitialBalance).teamId(1L).build();
        user = User.builder().id(1L).email("enesbyc19@gmail.com").password("123456").build();
        userDTO = UserDTO.builder().email("enesbyc19@gmail.com").password("123456").build();
        teamDTO = TeamDTO.builder().id(1L).name("Enes Team").balance(SoccerConst.teamInitialBalance)
                        .country("Turkey").playerValue(20 * SoccerConst.singlePlayerInitialBalance)
                        .build();

    }

    @Test
    public void givenNonExistsUserToken_whenSave_thenReturnResourceNotFoundException() {

        //given - precondition or setup
        given(jwtUtil.getUsernameFromToken()).willReturn(user.getEmail());
        given(userService.getByEmail(user.getEmail())).willReturn(null);
        //when - action or the behaviour that we are going test

        assertThrows(ResourceNotFoundException.class, () -> {
            playerService.save(playerDTO);
        });

        //then - verify the output
        verify(playerRepository, never()).save(any());

    }

    @Test
    public void givenNonExistsTeamWithUserToken_whenSave_thenReturnResourceNotFoundException() {

        //given - precondition or setup
        given(jwtUtil.getUsernameFromToken()).willReturn(user.getEmail());
        given(userService.getByEmail(user.getEmail())).willReturn(user);
        given(jwtUtil.getIsAdminFromToken()).willReturn(false);
        given(teamService.getByUserId(user.getId())).willReturn(null);

        //when - action or the behaviour that we are going test
        assertThrows(ResourceNotFoundException.class, () -> {
            playerService.save(playerDTO);
        });

        //then - verify the output 
        verify(playerRepository, never()).save(any());

    }

    @Test
    public void givenPlayerWithUserToken_whenSave_thenReturnSavedPlayer() {

        //given - precondition or setup
        given(jwtUtil.getUsernameFromToken()).willReturn(user.getEmail());
        given(userService.getByEmail(user.getEmail())).willReturn(user);
        given(jwtUtil.getIsAdminFromToken()).willReturn(false);
        given(teamService.getByUserId(user.getId())).willReturn(teamDTO);
        given(playerMapper.toPlayerDTO(player)).willReturn(playerDTO);
        given(playerRepository.save(player)).willReturn(player);
        given(playerMapper.toPlayer(playerDTO)).willReturn(player);

        //when - action or the behaviour that we are going test
        PlayerDTO savedPlayer = playerService.save(playerDTO);

        //then - verify the output
        assertThat(savedPlayer).isNotNull();

    }

    @Test
    public void givenNonExistsTeamIdWithAdminToken_whenSave_thenThrowResourceNotFoundException() {

        //given - precondition or setup
        given(jwtUtil.getUsernameFromToken()).willReturn(user.getEmail());
        given(userService.getByEmail(user.getEmail())).willReturn(user);
        given(jwtUtil.getIsAdminFromToken()).willReturn(true);
        given(teamService.getByTeamId(teamDTO.getId())).willReturn(null);

        //when - action or the behaviour that we are going test
        assertThrows(ResourceNotFoundException.class, () -> {
            playerService.save(playerDTO);
        });

        //then - verify the output
        verify(playerRepository, never()).save(any());

    }

    @Test
    public void givenPlayer_whenSaveWithAdminToken_thenReturnPlayer() {

        //given - precondition or setup
        given(jwtUtil.getUsernameFromToken()).willReturn(user.getEmail());
        given(userService.getByEmail(user.getEmail())).willReturn(user);
        given(jwtUtil.getIsAdminFromToken()).willReturn(true);
        given(teamService.getByTeamId(teamDTO.getId())).willReturn(teamDTO);
        given(playerMapper.toPlayerDTO(player)).willReturn(playerDTO);
        given(playerRepository.save(player)).willReturn(player);
        given(playerMapper.toPlayer(playerDTO)).willReturn(player);

        //when - action or the behaviour that we are going test
        PlayerDTO savedPlayer = playerService.save(playerDTO);

        //then - verify the output
        assertThat(playerDTO).isNotNull();

    }

    @Test
    public void givenNonExistsUserToken_whenUpdate_thenReturnResourceNotFoundException() {

        //given - precondition or setup
        given(jwtUtil.getUsernameFromToken()).willReturn(user.getEmail());
        given(userService.getByEmail(user.getEmail())).willReturn(null);
        //when - action or the behaviour that we are going test

        assertThrows(ResourceNotFoundException.class, () -> {
            playerService.update(playerDTO);
        });

        //then - verify the output
        verify(playerRepository, never()).save(any());

    }

    @Test
    public void givenNonExistsTeamWithUserToken_whenUpdate_thenReturnResourceNotFoundException() {

        //given - precondition or setup
        given(jwtUtil.getUsernameFromToken()).willReturn(user.getEmail());
        given(userService.getByEmail(user.getEmail())).willReturn(user);
        given(jwtUtil.getIsAdminFromToken()).willReturn(false);
        given(teamService.getByUserId(user.getId())).willReturn(null);

        //when - action or the behaviour that we are going test
        assertThrows(ResourceNotFoundException.class, () -> {
            playerService.update(playerDTO);
        });

        //then - verify the output
        verify(playerRepository, never()).save(any());

    }

    @Test
    public void givenNonExistsPlayerID_whenUpdate_thenReturnResourceNotFoundException() {

        //given - precondition or setup
        given(jwtUtil.getUsernameFromToken()).willReturn(user.getEmail());
        given(userService.getByEmail(user.getEmail())).willReturn(user);
        given(jwtUtil.getIsAdminFromToken()).willReturn(false);
        given(teamService.getByUserId(user.getId())).willReturn(teamDTO);
        given(playerRepository.findById(playerDTO.getId())).willReturn(Optional.empty());
        //when - action or the behaviour that we are going test

        assertThrows(ResourceNotFoundException.class, () -> {
            playerService.update(playerDTO);
        });

        //then - verify the output
        verify(playerRepository, never()).save(any());

    }

    @Test
    public void givenPlayerWithUserToken_whenUpdate_thenReturnSavedPlayer() {

        //given - precondition or setup
        given(jwtUtil.getUsernameFromToken()).willReturn(user.getEmail());
        given(userService.getByEmail(user.getEmail())).willReturn(user);
        given(jwtUtil.getIsAdminFromToken()).willReturn(false);
        given(teamService.getByUserId(user.getId())).willReturn(teamDTO);
        given(playerMapper.toPlayerDTO(player)).willReturn(playerDTO);
        given(playerRepository.save(player)).willReturn(player);
        given(playerMapper.toPlayer(playerDTO)).willReturn(player);
        given(playerRepository.findById(playerDTO.getId())).willReturn(Optional.of(player));

        //when - action or the behaviour that we are going test
        PlayerDTO savedPlayer = playerService.update(playerDTO);

        //then - verify the output
        assertThat(savedPlayer).isNotNull();

    }

    @Test
    public void givenNonExistsTeamIdWithAdminToken_whenUpdate_thenReturnResourceNotFoundException() {

        //given - precondition or setup
        given(jwtUtil.getUsernameFromToken()).willReturn(user.getEmail());
        given(userService.getByEmail(user.getEmail())).willReturn(user);
        given(jwtUtil.getIsAdminFromToken()).willReturn(true);
        given(teamService.getByTeamId(teamDTO.getId())).willReturn(null);

        //when - action or the behaviour that we are going test
        assertThrows(ResourceNotFoundException.class, () -> {
            playerService.update(playerDTO);
        });

        //then - verify the output
        verify(playerRepository, never()).save(any());

    }

    @Test
    public void givenNonExistsPlayerId_whenDelete_thenReturnResourceNotFoundException() {

        //given - precondition or setup
        given(playerRepository.findById(player.getId())).willReturn(Optional.empty());

        //when - action or the behaviour that we are going test
        assertThrows(ResourceNotFoundException.class, () -> {
            playerService.delete(player.getId());
        });

        //then - verify the output
        verify(playerRepository, never()).delete(any());

    }

    @Test
    public void givenOtherTeamIdWithUserToken_whenDelete_thenReturnBadRequestException() {

        //given - precondition or setup
        given(playerRepository.findById(player.getId())).willReturn(Optional.of(player));
        given(jwtUtil.getIsAdminFromToken()).willReturn(false);
        teamDTO.setId(2L);
        given(teamService.getByEmail(user.getEmail())).willReturn(teamDTO);
        given(jwtUtil.getUsernameFromToken()).willReturn(user.getEmail());

        //when - action or the behaviour that we are going test
        assertThrows(BadRequestException.class, () -> {
            playerService.delete(player.getId());
        });

        //then - verify the output
        verify(playerRepository, never()).delete(any());

    }

    @Test
    public void givenPlayerIdWithUserToken_whenDelete_thenDeleteSuccess() {

        //given - precondition or setup
        given(playerRepository.findById(player.getId())).willReturn(Optional.of(player));
        given(jwtUtil.getIsAdminFromToken()).willReturn(false);
        given(teamService.getByEmail(user.getEmail())).willReturn(teamDTO);
        given(jwtUtil.getUsernameFromToken()).willReturn(user.getEmail());

        //when - action or the behaviour that we are going test
        playerService.delete(player.getId());

        //then - verify the output

    }

    @Test
    public void givenPlayerId_whenGetById_thenReturnPlayer() {

        //given - precondition or setup
        given(playerRepository.findById(player.getId())).willReturn(Optional.of(player));
        given(playerMapper.toPlayerDTO(player)).willReturn(playerDTO);

        //when - action or the behaviour that we are going test
        PlayerDTO returnedPlayer = playerService.getById(player.getId());

        //then - verify the output
        assertThat(returnedPlayer).isNotNull();

    }

    @Test
    public void givenNonExistsPlayerId_whenGetById_thenReturnNull() {

        //given - precondition or setup
        given(playerRepository.findById(player.getId())).willReturn(Optional.empty());

        //when - action or the behaviour that we are going test
        PlayerDTO returnedPlayer = playerService.getById(player.getId());

        //then - verify the output
        assertThat(returnedPlayer).isNull();

    }

    @Test
    public void givenUserToken_whenGet_thenReturnPlayerList() {

        //given - precondition or setup
        given(jwtUtil.getIsAdminFromToken()).willReturn(false);
        given(teamService.getByEmail(user.getEmail())).willReturn(teamDTO);
        given(jwtUtil.getUsernameFromToken()).willReturn(user.getEmail());
        List<Player> players = new ArrayList<Player>();
        players.add(player);
        List<PlayerDTO> playerDTOs = new ArrayList<PlayerDTO>();
        playerDTOs.add(playerDTO);
        given(playerRepository.findByTeamId(teamDTO.getId())).willReturn(players);
        given(playerMapper.toPlayerDTO(players)).willReturn(playerDTOs);

        //when - action or the behaviour that we are going test
        List<PlayerDTO> returnedPlayers = playerService.get();

        //then - verify the output
        assertThat(returnedPlayers).isNotNull();
        assertThat(returnedPlayers.size()).isEqualTo(1);

    }

    @Test
    public void givenAdminToken_whenGet_thenReturnPlayerList() {

        //given - precondition or setup
        given(jwtUtil.getIsAdminFromToken()).willReturn(true);
        List<Player> players = new ArrayList<Player>();
        players.add(player);
        List<PlayerDTO> playerDTOs = new ArrayList<PlayerDTO>();
        playerDTOs.add(playerDTO);
        given(playerRepository.findAll()).willReturn(players);
        given(playerMapper.toPlayerDTO(players)).willReturn(playerDTOs);

        //when - action or the behaviour that we are going test
        List<PlayerDTO> returnedPlayers = playerService.get();

        //then - verify the output
        assertThat(returnedPlayers).isNotNull();
        assertThat(returnedPlayers.size()).isEqualTo(1);

    }

    @Test
    public void givenUserToken_whenGenerateAndSavePlayer_thenReturnPlayerList() {

        //given - precondition or setup
        List<Player> players = new ArrayList<Player>();
        players.add(player);
        //        given(playerRepository.saveAll(players)).willReturn(players);
        PlayerUtil.initializePlayerValues();
        //when - action or the behaviour that we are going test
        List<Player> returnedPlayers = playerService.generateAndSavePlayer(1L);

        //then - verify the output
        assertThat(returnedPlayers).isNotNull();

    }

}
