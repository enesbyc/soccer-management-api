package com.soccer.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.soccer.management.model.Player;

/**
 * @author enes.boyaci
 */
@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    List<Player> findByTeamId(long teamId);

    Player findTopByOrderByIdDesc();

}