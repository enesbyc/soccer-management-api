package com.soccer.management.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soccer.management.consts.Role;
import com.soccer.management.consts.SoccerConst;
import com.soccer.management.dto.TeamDTO;
import com.soccer.management.exception.BadRequestException;
import com.soccer.management.exception.ResourceNotFoundException;
import com.soccer.management.exception.ValidationException;
import com.soccer.management.model.Team;
import com.soccer.management.model.User;
import com.soccer.management.model.mapper.TeamMapper;
import com.soccer.management.repository.TeamRepository;
import com.soccer.management.security.JwtUtil;
import com.soccer.management.service.ITeamService;
import com.soccer.management.service.IUserService;

/**
 * @author enes.boyaci
 */
@Service
public class TeamService implements ITeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMapper teamMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private IUserService userService;

    @Override
    @Transactional
    public TeamDTO save(TeamDTO teamDTO) {

        TeamDTO teamFromDB;

        if (!Objects.isNull(jwtUtil.getUsernameFromToken())) {
            boolean isAdmin = jwtUtil.getIsAdminFromToken();
            if (!isAdmin) {
                teamFromDB = getByEmail(jwtUtil.getUsernameFromToken());
                User user = userService.getByEmail(jwtUtil.getUsernameFromToken());
                teamDTO.setUserId(user.getId());
            } else {
                teamFromDB = getByUserId(teamDTO.getUserId());
                User user = userService.getByUserId(teamDTO.getUserId());
                if (Objects.isNull(user))
                    throw new BadRequestException("User not found by given user id!");
                if (Objects.nonNull(user) && !user.getRole().equals(Role.USER.name()))
                    throw new BadRequestException("Admin cannot have a team!");

            }
            if (Objects.nonNull(teamFromDB))
                throw new BadRequestException("User already has 1 team!");

        }
        Team team = teamMapper.toTeam(teamDTO);
        team.setBalance(SoccerConst.teamInitialBalance);
        team.setPlayerValue(0);
        team = teamRepository.save(team);
        TeamDTO savedTeam = teamMapper.toTeamDTO(team);
        return savedTeam;
    }

    @Override
    @Transactional
    public TeamDTO update(TeamDTO teamDTO) {

        TeamDTO teamFromDB;
        boolean isAdmin = jwtUtil.getIsAdminFromToken();
        if (!isAdmin) {
            teamFromDB = getByEmail(jwtUtil.getUsernameFromToken());
            if (Objects.nonNull(teamFromDB))
                if (teamFromDB.getId() != teamDTO.getId())
                    throw new BadRequestException("The user can only update his own team!");
        } else {
            if (Objects.isNull(teamDTO.getUserId()) || teamDTO.getUserId() <= 0)
                throw new ValidationException("User id must be greater than 0!");
            teamFromDB = getByUserId(teamDTO.getUserId());
        }

        if (Objects.isNull(teamFromDB))
            throw new ResourceNotFoundException("Team not found!");

        //Only these two fields can be updated.
        teamFromDB.setCountry(teamDTO.getCountry());
        teamFromDB.setName(teamDTO.getName());

        Team team = teamMapper.toTeam(teamFromDB);
        team = teamRepository.save(team);
        return teamMapper.toTeamDTO(team);
    }

    @Override
    @Transactional
    public TeamDTO getByUserId(long userId) {
        Optional<Team> team = teamRepository.findByUserId(userId);
        if (team.isPresent())
            return teamMapper.toTeamDTO(team.get());
        else
            return null;
    }

    @Override
    @Transactional
    public void delete(Long teamId) {
        TeamDTO teamFromDB;
        boolean isAdmin = jwtUtil.getIsAdminFromToken();
        if (!isAdmin) {
            teamFromDB = getByEmail(jwtUtil.getUsernameFromToken());
            if (Objects.nonNull(teamFromDB) && teamFromDB.getId() != teamId)
                throw new BadRequestException("The user can only delete his own team!");
        }

        boolean isExists = teamRepository.existsById(teamId);
        if (!isExists)
            throw new ResourceNotFoundException("Team not found with given id!");

        teamRepository.deleteById(teamId);

    }

    @Override
    @Transactional
    public TeamDTO getByEmail(String email) {
        Team team = teamRepository.findByEmail(email);
        if (Objects.nonNull(team))
            return teamMapper.toTeamDTO(team);
        else
            return null;
    }

    @Override
    @Transactional
    public void calculateAndUpdatePlayerValue(long teamId) {
        Optional<Team> teamObj = teamRepository.findById(teamId);
        if (!teamObj.isPresent())
            throw new ResourceNotFoundException("Team not found!");
        teamRepository.updateTeamValue(teamId);

    }

    @Override
    @Transactional
    public TeamDTO generateAndSaveTeam(long userId) {
        TeamDTO team = TeamDTO.builder().balance(SoccerConst.teamInitialBalance).country("Turkey")
                        .name("Team").playerValue(20 * SoccerConst.singlePlayerInitialBalance)
                        .userId(userId).build();
        TeamDTO savedTeamDTO = save(team);
        return savedTeamDTO;
    }

    @Override
    @Transactional
    public TeamDTO getByTeamId(long teamId) {
        Optional<Team> team = teamRepository.findById(teamId);
        if (team.isPresent())
            return teamMapper.toTeamDTO(team.get());
        else
            return null;
    }

    @Override
    @Transactional
    public List<TeamDTO> get() {

        boolean isAdmin = jwtUtil.getIsAdminFromToken();
        List<TeamDTO> teams = new ArrayList<TeamDTO>();
        if (!isAdmin) {
            TeamDTO teamDTO = getByEmail(jwtUtil.getUsernameFromToken());
            teams.add(teamDTO);
        } else {
            teams = teamMapper.toTeamDTO(teamRepository.findAll());
        }
        return teams;
    }

    @Override
    @Transactional
    public void deleteAll() {
        teamRepository.deleteAll();
    }

}
