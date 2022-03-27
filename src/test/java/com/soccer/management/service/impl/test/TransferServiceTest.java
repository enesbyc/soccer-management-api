package com.soccer.management.service.impl.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.soccer.management.consts.SoccerConst;
import com.soccer.management.dto.PlayerDTO;
import com.soccer.management.dto.TeamDTO;
import com.soccer.management.dto.TransferDTO;
import com.soccer.management.dto.UserDTO;
import com.soccer.management.exception.BadRequestException;
import com.soccer.management.exception.ResourceNotFoundException;
import com.soccer.management.model.Player;
import com.soccer.management.model.Team;
import com.soccer.management.model.Transfer;
import com.soccer.management.model.User;
import com.soccer.management.model.mapper.TransferMapper;
import com.soccer.management.repository.PlayerRepository;
import com.soccer.management.repository.TransferRepository;
import com.soccer.management.security.JwtUtil;
import com.soccer.management.service.impl.PlayerService;
import com.soccer.management.service.impl.TeamService;
import com.soccer.management.service.impl.TransferService;
import com.soccer.management.service.impl.UserService;

@ExtendWith(MockitoExtension.class)
public class TransferServiceTest {

    @Mock
    private TransferRepository transferRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private TransferMapper transferMapper;

    @Mock
    private PlayerService playerService;

    @Mock
    private UserService userService;

    @InjectMocks
    private TransferService transferService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private TeamService teamService;

    private Player player;
    private PlayerDTO playerDTO;
    private User user;
    private UserDTO userDTO;
    private TeamDTO teamDTO;
    private Team team;
    private Transfer transfer;
    private TransferDTO transferDTO;

    @BeforeEach
    public void setup() {
        team = Team.builder().id(1L).name("Enes Team").balance(SoccerConst.teamInitialBalance)
                        .country("Turkey").playerValue(20 * SoccerConst.singlePlayerInitialBalance)
                        .build();
        player = Player.builder().age(20).country(SoccerConst.defaultCountry).firstName("Enes")
                        .lastName("Boyaci").marketValue(SoccerConst.singlePlayerInitialBalance)
                        .teamId(team.getId()).team(team).build();
        playerDTO = playerDTO.builder().age(20).country(SoccerConst.defaultCountry)
                        .firstName("Enes").lastName("Boyaci")
                        .marketValue(SoccerConst.singlePlayerInitialBalance).teamId(1L).build();
        user = User.builder().id(1L).email("enesbyc19@gmail.com").password("123456").build();
        userDTO = UserDTO.builder().email("enesbyc19@gmail.com").password("123456").build();
        teamDTO = TeamDTO.builder().id(1L).name("Enes Team").balance(SoccerConst.teamInitialBalance)
                        .country("Turkey").playerValue(20 * SoccerConst.singlePlayerInitialBalance)
                        .build();

        transfer = Transfer.builder().id(1L).playerId(1L).teamId(1L).transferAmount(100000)
                        .transferStartDate(new Date()).build();
        transferDTO = TransferDTO.builder().playerId(1L).transferAmount(new Double(10000))
                        .transferStartDate(new Date()).build();
    }

    @Test
    public void givenNonExistsPlayerID_whenTransferPlayer_thenReturnResourceNotFoundException() {

        //given - precondition or setup
        given(playerService.getById(transfer.getId())).willReturn(null);
        given(jwtUtil.getIsAdminFromToken()).willReturn(false);
        //when - action or the behaviour that we are going test

        assertThrows(ResourceNotFoundException.class, () -> {
            transferService.transferPlayer(transferDTO);
        });

        //then - verify the output
        verify(transferRepository, never()).save(any());

    }

    @Test
    public void givenAlreadyWaitingTransferPlayerID_whenTransferPlayer_thenReturnBadRequestException() {

        //given - precondition or setup
        given(playerService.getById(transfer.getId())).willReturn(playerDTO);
        given(transferRepository.findByPlayerId(player.getId())).willReturn(transfer);
        //when - action or the behaviour that we are going test

        assertThrows(BadRequestException.class, () -> {
            transferService.transferPlayer(transferDTO);
        });

        //then - verify the output
        verify(transferRepository, never()).save(any());

    }

    @Test
    public void givenNonExistsUserToken_whenTransferPlayer_thenReturnBadRequestException() {

        //given - precondition or setup
        given(playerService.getById(transfer.getId())).willReturn(playerDTO);
        given(transferRepository.findByPlayerId(player.getId())).willReturn(transfer);
        given(jwtUtil.getIsAdminFromToken()).willReturn(false);
        //        given(jwtUtil.getUsernameFromToken()).willReturn(user.getEmail());
        //        given(userService.getByEmail(user.getEmail())).willReturn(null);
        //when - action or the behaviour that we are going test

        assertThrows(BadRequestException.class, () -> {
            transferService.transferPlayer(transferDTO);
        });

        //then - verify the output
        verify(transferRepository, never()).save(any());

    }

    @Test
    public void givenAnotherTeamPlayerId_whenTransferPlayer_thenReturnBadRequestException() {

        //given - precondition or setup
        given(playerService.getById(transfer.getId())).willReturn(playerDTO);
        given(transferRepository.findByPlayerId(player.getId())).willReturn(null);
        given(jwtUtil.getIsAdminFromToken()).willReturn(false);
        given(jwtUtil.getUsernameFromToken()).willReturn(user.getEmail());
        given(userService.getByEmail(user.getEmail())).willReturn(null);
        //when - action or the behaviour that we are going test

        assertThrows(BadRequestException.class, () -> {
            transferService.transferPlayer(transferDTO);
        });

        //then - verify the output
        verify(transferRepository, never()).save(any());

    }

    @Test
    public void givenNonExistsTeam_whenTransferPlayer_thenReturnResourceNotFoundException() {

        //given - precondition or setup
        given(playerService.getById(transfer.getId())).willReturn(playerDTO);
        given(transferRepository.findByPlayerId(player.getId())).willReturn(null);
        given(jwtUtil.getIsAdminFromToken()).willReturn(false);
        given(jwtUtil.getUsernameFromToken()).willReturn(user.getEmail());
        given(userService.getByEmail(user.getEmail())).willReturn(user);
        given(teamService.getByUserId(user.getId())).willReturn(null);
        //when - action or the behaviour that we are going test

        assertThrows(ResourceNotFoundException.class, () -> {
            transferService.transferPlayer(transferDTO);
        });

        //then - verify the output
        verify(transferRepository, never()).save(any());

    }

    @Test
    public void givenAnotherTeamId_whenTransferPlayer_thenReturnBadRequestException() {

        //given - precondition or setup
        player.setTeamId(5L);
        playerDTO.setTeamId(5L);
        given(playerService.getById(transfer.getId())).willReturn(playerDTO);
        given(transferRepository.findByPlayerId(player.getId())).willReturn(null);
        given(jwtUtil.getIsAdminFromToken()).willReturn(false);
        given(jwtUtil.getUsernameFromToken()).willReturn(user.getEmail());
        given(userService.getByEmail(user.getEmail())).willReturn(user);
        given(teamService.getByUserId(user.getId())).willReturn(teamDTO);
        //when - action or the behaviour that we are going test

        assertThrows(BadRequestException.class, () -> {
            transferService.transferPlayer(transferDTO);
        });

        //then - verify the output
        verify(transferRepository, never()).save(any());

    }

    @Test
    public void givenTransfer_whenTransferPlayer_thenReturnTransfer() {

        //given - precondition or setup
        given(playerService.getById(transfer.getId())).willReturn(playerDTO);
        given(transferRepository.findByPlayerId(player.getId())).willReturn(null);
        given(jwtUtil.getIsAdminFromToken()).willReturn(false);
        given(jwtUtil.getUsernameFromToken()).willReturn(user.getEmail());
        given(userService.getByEmail(user.getEmail())).willReturn(user);
        given(teamService.getByUserId(user.getId())).willReturn(teamDTO);
        given(transferMapper.toTransfer(transferDTO)).willReturn(transfer);
        given(transferRepository.save(transfer)).willReturn(transfer);
        given(transferMapper.toTransferDTO(transfer)).willReturn(transferDTO);

        //when - action or the behaviour that we are going test
        TransferDTO transferredDTO = transferService.transferPlayer(transferDTO);

        //then - verify the output
        assertThat(transferredDTO).isNotNull();
        assertThat(transferredDTO.getTransferStartDate()).isNotNull();

    }

    @Test
    public void givenAdminToken_whenBuyPlayer_thenReturnBadRequestException() {

        //given - precondition or setup
        given(jwtUtil.getIsAdminFromToken()).willReturn(true);

        //when - action or the behaviour that we are going test
        assertThrows(BadRequestException.class, () -> {
            transferService.buyPlayer(player.getId());
        });

        //then - verify the output
        verify(teamService, never()).update(any());
        verify(playerService, never()).update(any());
    }

    @Test
    public void givenNonExistsPlayerIdOnTransferList_whenBuyPlayer_thenReturnBadRequestException() {

        //given - precondition or setup
        given(jwtUtil.getIsAdminFromToken()).willReturn(false);
        given(transferRepository.findByPlayerId(player.getId())).willReturn(null);

        //when - action or the behaviour that we are going test
        assertThrows(BadRequestException.class, () -> {
            transferService.buyPlayer(player.getId());
        });

        //then - verify the output
        verify(teamService, never()).update(any());
        verify(playerService, never()).update(any());
    }

    @Test
    public void givenNonExistsUserToken_whenBuyPlayer_thenReturnBadRequestException() {

        //given - precondition or setup
        given(jwtUtil.getIsAdminFromToken()).willReturn(false);
        given(transferRepository.findByPlayerId(player.getId())).willReturn(transfer);
        given(jwtUtil.getUsernameFromToken()).willReturn(user.getEmail());
        given(userService.getByEmail(user.getEmail())).willReturn(null);

        //when - action or the behaviour that we are going test
        assertThrows(ResourceNotFoundException.class, () -> {
            transferService.buyPlayer(player.getId());
        });

        //then - verify the output
        verify(teamService, never()).update(any());
        verify(playerService, never()).update(any());
    }

    @Test
    public void givenNonExistsTeam_whenBuyPlayer_thenReturnBadRequestException() {

        //given - precondition or setup
        given(jwtUtil.getIsAdminFromToken()).willReturn(false);
        given(transferRepository.findByPlayerId(player.getId())).willReturn(transfer);
        given(jwtUtil.getUsernameFromToken()).willReturn(user.getEmail());
        given(userService.getByEmail(user.getEmail())).willReturn(user);
        given(teamService.getByUserId(user.getId())).willReturn(null);

        //when - action or the behaviour that we are going test
        assertThrows(ResourceNotFoundException.class, () -> {
            transferService.buyPlayer(player.getId());
        });

        //then - verify the output
        verify(teamService, never()).update(any());
        verify(playerService, never()).update(any());
    }

    @Test
    public void givenBuyOwnPlayer_whenBuyPlayer_thenReturnBadRequestException() {

        //given - precondition or setup
        given(jwtUtil.getIsAdminFromToken()).willReturn(false);
        given(transferRepository.findByPlayerId(player.getId())).willReturn(transfer);
        given(jwtUtil.getUsernameFromToken()).willReturn(user.getEmail());
        given(userService.getByEmail(user.getEmail())).willReturn(user);
        given(teamService.getByUserId(user.getId())).willReturn(teamDTO);

        //when - action or the behaviour that we are going test
        assertThrows(BadRequestException.class, () -> {
            transferService.buyPlayer(player.getId());
        });

        //then - verify the output
        verify(teamService, never()).update(any());
        verify(playerService, never()).update(any());
    }

    @Test
    public void givenInsufficentBalance_whenBuyPlayer_thenReturnBadRequestException() {

        //given - precondition or setup
        teamDTO.setBalance(0);
        team.setId(1L);
        transfer.setTeamId(2L);

        given(jwtUtil.getIsAdminFromToken()).willReturn(false);
        given(transferRepository.findByPlayerId(player.getId())).willReturn(transfer);
        given(jwtUtil.getUsernameFromToken()).willReturn(user.getEmail());
        given(userService.getByEmail(user.getEmail())).willReturn(user);
        given(teamService.getByUserId(user.getId())).willReturn(teamDTO);

        //when - action or the behaviour that we are going test
        assertThrows(BadRequestException.class, () -> {
            transferService.buyPlayer(player.getId());
        });

        //then - verify the output
        verify(teamService, never()).update(any());
        verify(playerService, never()).update(any());
    }

    @Test
    public void givenTeam_whenBuyPlayer_thenReturnPlayer() {

        //given - precondition or setup
        teamDTO.setBalance(SoccerConst.teamInitialBalance);
        team.setId(1L);
        transfer.setTeamId(2L);
        given(jwtUtil.getIsAdminFromToken()).willReturn(false);
        given(transferRepository.findByPlayerId(player.getId())).willReturn(transfer);
        given(jwtUtil.getUsernameFromToken()).willReturn(user.getEmail());
        given(userService.getByEmail(user.getEmail())).willReturn(user);
        given(teamService.getByUserId(user.getId())).willReturn(teamDTO);
        given(playerService.getById(player.getId())).willReturn(playerDTO);
        given(teamService.getByTeamId(player.getTeamId())).willReturn(teamDTO);
        given(playerService.update(playerDTO)).willReturn(playerDTO);
        given(transferRepository.existsById(transfer.getId())).willReturn(true);
        //when - action or the behaviour that we are going test
        PlayerDTO transferredPlayer = transferService.buyPlayer(player.getId());

        //then - verify the output
        assertThat(transferredPlayer).isNotNull();

    }

}
