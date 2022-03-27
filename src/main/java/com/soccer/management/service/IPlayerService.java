package com.soccer.management.service;

import java.util.List;

import com.soccer.management.dto.PlayerDTO;
import com.soccer.management.model.Player;

/**
 * @author enes.boyaci
 */
public interface IPlayerService {

    List<Player> generateAndSavePlayer(long teamId);

    PlayerDTO save(PlayerDTO playerDTO);

    PlayerDTO getById(long playerId);

    PlayerDTO update(PlayerDTO player);

    void delete(Long playerId);

    List<PlayerDTO> get();

}
