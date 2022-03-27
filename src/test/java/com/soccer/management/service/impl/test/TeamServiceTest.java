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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.soccer.management.consts.SoccerConst;
import com.soccer.management.dto.TeamDTO;
import com.soccer.management.dto.UserDTO;
import com.soccer.management.exception.BadRequestException;
import com.soccer.management.exception.ResourceNotFoundException;
import com.soccer.management.exception.ValidationException;
import com.soccer.management.model.Team;
import com.soccer.management.model.User;
import com.soccer.management.model.mapper.TeamMapper;
import com.soccer.management.repository.TeamRepository;
import com.soccer.management.security.JwtUtil;
import com.soccer.management.service.impl.PlayerService;
import com.soccer.management.service.impl.TeamService;
import com.soccer.management.service.impl.UserService;

@ExtendWith(MockitoExtension.class)
public class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TeamMapper teamMapper;

    @InjectMocks
    private TeamService teamService;

    @Mock
    private PlayerService playerService;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    private User user;
    private UserDTO userDTO;
    private TeamDTO teamDTO;
    private Team team;

    @BeforeEach
    public void setup() {
        user = User.builder().id(1L).email("enesbyc19@gmail.com").password("123456").build();
        userDTO = UserDTO.builder().email("enesbyc19@gmail.com").password("123456").build();
        teamDTO = TeamDTO.builder().id(1L).name("Enes Team").balance(SoccerConst.teamInitialBalance)
                        .country("Turkey").playerValue(20 * SoccerConst.singlePlayerInitialBalance)
                        .build();
        team = Team.builder().id(1L).name("Enes Team").balance(SoccerConst.teamInitialBalance)
                        .country("Turkey").playerValue(20 * SoccerConst.singlePlayerInitialBalance)
                        .build();
    }

    @Test
    @DisplayName("JUnit test for save user with given exists email")
    public void givenAlreadyHasTeamUserWithUserToken_whenSave_thenReturnBadRequestException() {

        //given - precondition or setup
        given(jwtUtil.getIsAdminFromToken()).willReturn(false);
        given(jwtUtil.getUsernameFromToken()).willReturn(user.getEmail());
        given(teamRepository.findByEmail(user.getEmail())).willReturn(team);
        given(teamMapper.toTeamDTO(team)).willReturn(teamDTO);
        given(userService.getByEmail(user.getEmail())).willReturn(user);

        //when - action or the behaviour that we are going test
        assertThrows(BadRequestException.class, () -> {
            teamService.save(teamDTO);
        });

        //then - verify the output 
        verify(teamRepository, never()).save(team);

    }

    @Test
    public void givenUserHasNoTeamWithUserToken_whenSave_thenReturnTeam() {

        //given - precondition or setup
        given(jwtUtil.getIsAdminFromToken()).willReturn(false);
        given(jwtUtil.getUsernameFromToken()).willReturn(user.getEmail());
        given(teamRepository.findByEmail(user.getEmail())).willReturn(null);
        given(userService.getByEmail(user.getEmail())).willReturn(user);
        given(teamMapper.toTeam(teamDTO)).willReturn(team);
        given(teamMapper.toTeamDTO(team)).willReturn(teamDTO);
        given(teamRepository.save(team)).willReturn(team);
        //when - action or the behaviour that we are going test
        TeamDTO savedTeam = teamService.save(teamDTO);

        //then - verify the output
        assertThat(savedTeam).isNotNull();

    }

    @Test
    public void givenOtherUserTeamWithUserToken_whenUpdate_thenReturnBadRequestException() {

        //given - precondition or setup
        given(jwtUtil.getIsAdminFromToken()).willReturn(false);
        given(jwtUtil.getUsernameFromToken()).willReturn(user.getEmail());
        given(teamRepository.findByEmail(user.getEmail())).willReturn(team);
        given(teamMapper.toTeamDTO(team)).willReturn(teamDTO);
        TeamDTO otherTeam = TeamDTO.builder().id(3L).name("Enes2 Team")
                        .balance(SoccerConst.teamInitialBalance).country("Turkey")
                        .playerValue(20 * SoccerConst.singlePlayerInitialBalance).build();

        //when - action or the behaviour that we are going test
        assertThrows(BadRequestException.class, () -> {
            teamService.update(otherTeam);
        });

        //then - verify the output 
        verify(teamRepository, never()).save(team);

    }

    @Test
    public void givenNonExistsTeamWithUserToken_whenUpdate_thenReturnResourceNotFoundException() {

        //given - precondition or setup
        given(jwtUtil.getIsAdminFromToken()).willReturn(false);
        given(jwtUtil.getUsernameFromToken()).willReturn(user.getEmail());
        given(teamRepository.findByEmail(user.getEmail())).willReturn(null);

        //when - action or the behaviour that we are going test
        assertThrows(ResourceNotFoundException.class, () -> {
            teamService.update(teamDTO);
        });

        //then - verify the output 
        verify(teamRepository, never()).save(team);

    }

    @Test
    public void givenEmptyTeamIdTeamWithAdminToken_whenUpdate_thenReturnResourceNotFoundException() {

        //given - precondition or setup
        given(jwtUtil.getIsAdminFromToken()).willReturn(true);
        teamDTO.setId(0);
        //when - action or the behaviour that we are going test
        assertThrows(ValidationException.class, () -> {
            teamService.update(teamDTO);
        });

        //then - verify the output 
        verify(teamRepository, never()).save(team);

    }

    @Test
    public void givenTeamWithAdminToken_whenUpdate_thenReturnTeam() {

        //given - precondition or setup
        teamDTO.setUserId(1L);
        given(jwtUtil.getIsAdminFromToken()).willReturn(true);
        given(teamRepository.findByUserId(user.getId())).willReturn(Optional.of(team));
        given(teamRepository.save(team)).willReturn(team);
        given(teamMapper.toTeamDTO(team)).willReturn(teamDTO);
        given(teamMapper.toTeam(teamDTO)).willReturn(team);
        given(teamRepository.save(team)).willReturn(team);
        //when - action or the behaviour that we are going test
        teamDTO.setCountry("CountryChange");
        TeamDTO updatedTeam = teamService.update(teamDTO);

        //then - verify the output 
        assertThat(updatedTeam).isNotNull();
        assertThat(updatedTeam.getCountry()).isEqualTo("CountryChange");

    }

    @Test
    public void givenNonExistsTeamId_whenCalculateAndUpdatePlayerValue_thenReturnResourceNotFoundException() {

        //given - precondition or setup
        given(teamRepository.findById(team.getId())).willReturn(Optional.empty());

        //when - action or the behaviour that we are going test
        assertThrows(ResourceNotFoundException.class, () -> {
            teamService.calculateAndUpdatePlayerValue(team.getId());
        });

        //then - verify the output 
        verify(teamRepository, never()).updateTeamValue(team.getId());

    }

    @Test
    public void givenExistsTeamId_whenCalculateAndUpdatePlayerValue_thenSuccess() {

        //given - precondition or setup
        given(teamRepository.findById(team.getId())).willReturn(Optional.of(team));

        //when - action or the behaviour that we are going test
        teamService.calculateAndUpdatePlayerValue(team.getId());

        //then - verify the output 

    }

    @Test
    public void givenExistsTeamId_whenGetByTeamId_thenSuccess() {

        //given - precondition or setup
        given(teamRepository.findById(team.getId())).willReturn(Optional.of(team));
        given(teamMapper.toTeamDTO(team)).willReturn(teamDTO);
        //when - action or the behaviour that we are going test
        TeamDTO returnedTeam = teamService.getByTeamId(team.getId());

        //then - verify the output 
        assertThat(returnedTeam).isNotNull();
    }

    @Test
    public void givenNonExistsTeamId_whenGetByTeamId_thenReturnNull() {

        //given - precondition or setup
        given(teamRepository.findById(team.getId())).willReturn(Optional.empty());

        //when - action or the behaviour that we are going test
        TeamDTO returnedTeam = teamService.getByTeamId(team.getId());

        //then - verify the output 
        assertThat(returnedTeam).isNull();
    }

    @Test
    public void givenTeamListWithUserToken_whenGet_thenReturnTeamList() {

        //given - precondition or setup
        given(teamRepository.findByEmail(user.getEmail())).willReturn(team);
        given(teamMapper.toTeamDTO(team)).willReturn(teamDTO);
        given(jwtUtil.getIsAdminFromToken()).willReturn(false);
        given(jwtUtil.getUsernameFromToken()).willReturn(user.getEmail());
        List<Team> teamList = new ArrayList<Team>();
        teamList.add(team);
        List<TeamDTO> teamDTOList = new ArrayList<TeamDTO>();
        teamDTOList.add(teamDTO);

        //when - action or the behaviour that we are going test
        List<TeamDTO> returnedTeams = teamService.get();

        //then - verify the output 
        assertThat(returnedTeams).isNotNull();
        assertThat(returnedTeams.size()).isEqualTo(1);
    }

    @Test
    public void givenTeamListWithAdminToken_whenGet_thenReturnTeamList() {

        //given - precondition or setup
        given(jwtUtil.getIsAdminFromToken()).willReturn(true);
        List<Team> teamList = new ArrayList<Team>();
        teamList.add(team);
        List<TeamDTO> teamDTOList = new ArrayList<TeamDTO>();
        teamDTOList.add(teamDTO);
        given(teamRepository.findAll()).willReturn(teamList);
        given(teamMapper.toTeamDTO(teamList)).willReturn(teamDTOList);

        //when - action or the behaviour that we are going test
        List<TeamDTO> returnedTeams = teamService.get();

        //then - verify the output 
        assertThat(returnedTeams).isNotNull();
        assertThat(returnedTeams.size()).isEqualTo(1);
    }

    @Test
    public void givenNonExistsTeamWithAdminToken_whenDelete_thenReturnResourceNotFoundException() {

        //given - precondition or setup
        given(jwtUtil.getIsAdminFromToken()).willReturn(true);
        given(teamRepository.existsById(team.getId())).willReturn(false);

        //when - action or the behaviour that we are going test
        assertThrows(ResourceNotFoundException.class, () -> {
            teamService.delete(team.getId());
        });

        //then - verify the output 
        verify(teamRepository, never()).deleteById(any());
    }

    @Test
    public void givenExistsTeamWithAdminToken_whenDelete_thenReturnSuccess() {

        //given - precondition or setup
        given(jwtUtil.getIsAdminFromToken()).willReturn(true);
        given(teamRepository.existsById(team.getId())).willReturn(true);

        //when - action or the behaviour that we are going test
        teamService.delete(team.getId());

        //then - verify the output 
    }

}
