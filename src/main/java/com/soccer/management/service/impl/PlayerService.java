package com.soccer.management.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soccer.management.consts.PlayerType;
import com.soccer.management.dto.PlayerDTO;
import com.soccer.management.dto.TeamDTO;
import com.soccer.management.exception.BadRequestException;
import com.soccer.management.exception.ResourceNotFoundException;
import com.soccer.management.model.Player;
import com.soccer.management.model.User;
import com.soccer.management.model.mapper.PlayerMapper;
import com.soccer.management.repository.PlayerRepository;
import com.soccer.management.security.JwtUtil;
import com.soccer.management.service.IPlayerService;
import com.soccer.management.service.ITeamService;
import com.soccer.management.service.IUserService;
import com.soccer.management.util.PlayerUtil;

/**
 * @author enes.boyaci
 */
@Service
public class PlayerService implements IPlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerMapper playerMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private IUserService userService;

    @Autowired
    private ITeamService teamService;

    @Override
    @Transactional
    public PlayerDTO save(PlayerDTO playerDTO) {

        User user = userService.getByEmail(jwtUtil.getUsernameFromToken());

        if (Objects.isNull(user))
            throw new ResourceNotFoundException("User not found!");

        TeamDTO team;
        boolean isAdmin = jwtUtil.getIsAdminFromToken();
        if (!isAdmin) {
            team = teamService.getByUserId(user.getId());
            if (Objects.isNull(team))
                throw new ResourceNotFoundException(
                                "Team not found! You need to add team before add player.");

            playerDTO.setTeamId(team.getId());
        } else {
            team = teamService.getByTeamId(playerDTO.getTeamId());
            if (Objects.isNull(team))
                throw new ResourceNotFoundException("Team not found!");
        }

        Player player = playerMapper.toPlayer(playerDTO);
        player = playerRepository.save(player);
        playerDTO = playerMapper.toPlayerDTO(player);
        teamService.calculateAndUpdatePlayerValue(playerDTO.getTeamId());

        return playerDTO;
    }

    @Override
    @Transactional
    public PlayerDTO update(PlayerDTO playerDTO) {

        User user = userService.getByEmail(jwtUtil.getUsernameFromToken());

        if (Objects.isNull(user))
            throw new ResourceNotFoundException("User not found!");

        boolean isAdmin = jwtUtil.getIsAdminFromToken();
        if (!isAdmin) {
            TeamDTO team = teamService.getByUserId(user.getId());
            if (Objects.isNull(team))
                throw new ResourceNotFoundException(
                                "Team not found! You need to add team before update player.");

            if (team.getId() != playerDTO.getTeamId())
                throw new BadRequestException("Only own player will be update!");

            playerDTO.setTeamId(team.getId());

        } else {
            TeamDTO team = teamService.getByTeamId(playerDTO.getTeamId());
            if (Objects.isNull(team))
                throw new ResourceNotFoundException("Team not found!");

        }

        Optional<Player> playerFromDB = playerRepository.findById(playerDTO.getId());
        if (!playerFromDB.isPresent())
            throw new ResourceNotFoundException("Player not found with given id!");

        Player player = playerMapper.toPlayer(playerDTO);
        player = playerRepository.save(player);
        teamService.calculateAndUpdatePlayerValue(playerDTO.getTeamId());

        return playerMapper.toPlayerDTO(player);

    }

    @Override
    @Transactional
    public void delete(Long playerId) {

        Optional<Player> player = playerRepository.findById(playerId);
        if (!player.isPresent())
            throw new ResourceNotFoundException("Player not found by given id!");

        boolean isAdmin = jwtUtil.getIsAdminFromToken();
        if (!isAdmin) {
            TeamDTO team = teamService.getByEmail(jwtUtil.getUsernameFromToken());
            if (team.getId() != player.get().getTeamId())
                throw new BadRequestException("Only own player can be delete!");
        }
        playerRepository.deleteById(playerId);
        teamService.calculateAndUpdatePlayerValue(player.get().getTeamId());

    }

    @Override
    @Transactional
    public List<Player> generateAndSavePlayer(long teamId) {

        List<Player> players = new ArrayList<Player>();
        for (int i = 0; i < 3; i++)
            players.add(PlayerUtil.generatePlayer(teamId, PlayerType.GOALKEEPER.getType()));
        for (int i = 0; i < 6; i++)
            players.add(PlayerUtil.generatePlayer(teamId, PlayerType.DEFENDER.getType()));
        for (int i = 0; i < 6; i++)
            players.add(PlayerUtil.generatePlayer(teamId, PlayerType.MIDFIELDER.getType()));
        for (int i = 0; i < 5; i++)
            players.add(PlayerUtil.generatePlayer(teamId, PlayerType.MIDFIELDER.getType()));
        players = playerRepository.saveAll(players);

        return players;

    }

    @Override
    @Transactional
    public PlayerDTO getById(long playerId) {

        Optional<Player> player = playerRepository.findById(playerId);
        if (player.isPresent())
            return playerMapper.toPlayerDTO(player.get());
        else
            return null;
    }

    @Override
    @Transactional
    public List<PlayerDTO> get() {

        boolean isAdmin = jwtUtil.getIsAdminFromToken();
        List<Player> players = new ArrayList<Player>();
        if (!isAdmin) {
            TeamDTO teamDTO = teamService.getByEmail(jwtUtil.getUsernameFromToken());
            if (Objects.nonNull(teamDTO))
                players = playerRepository.findByTeamId(teamDTO.getId());
        } else
            players = playerRepository.findAll();
        return playerMapper.toPlayerDTO(players);
    }

}
