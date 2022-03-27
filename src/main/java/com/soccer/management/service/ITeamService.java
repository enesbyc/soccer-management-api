package com.soccer.management.service;

import java.util.List;

import com.soccer.management.dto.TeamDTO;

/**
 * @author enes.boyaci
 */
public interface ITeamService {

    TeamDTO save(TeamDTO team);

    TeamDTO generateAndSaveTeam(long userId);

    TeamDTO getByTeamId(long teamId);

    TeamDTO update(TeamDTO team);

    TeamDTO getByUserId(long id);

    void delete(Long teamId);

    TeamDTO getByEmail(String usernameFromToken);

    void calculateAndUpdatePlayerValue(long teamId);

    List<TeamDTO> get();

    void deleteAll();

}
