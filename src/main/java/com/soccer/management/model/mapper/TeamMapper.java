package com.soccer.management.model.mapper;

import java.util.Collection;
import java.util.List;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;

import com.soccer.management.dto.TeamDTO;
import com.soccer.management.model.Team;

/**
 * @author enes.boyaci
 */
@Mapper(componentModel = "spring")
public interface TeamMapper {
    TeamDTO toTeamDTO(Team team);

    Team toTeam(TeamDTO teamDTO);

    @IterableMapping(qualifiedByName = {"toTeamDTO"})
    List<TeamDTO> toTeamDTO(Collection<Team> team);

}
