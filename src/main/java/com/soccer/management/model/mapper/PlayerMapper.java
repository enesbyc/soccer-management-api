package com.soccer.management.model.mapper;

import java.util.Collection;
import java.util.List;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;

import com.soccer.management.dto.PlayerDTO;
import com.soccer.management.model.Player;

/**
 * @author enes.boyaci
 */
@Mapper(componentModel = "spring")
public interface PlayerMapper {
    PlayerDTO toPlayerDTO(Player player);

    Player toPlayer(PlayerDTO playerDTO);

    @IterableMapping(qualifiedByName = {"toPlayerDTO"})
    List<PlayerDTO> toPlayerDTO(Collection<Player> player);

}
