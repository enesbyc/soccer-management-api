package com.soccer.management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.soccer.management.model.Team;

/**
 * @author enes.boyaci
 */
@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findByUserId(long userId);

    @Query("SELECT t from Team t , User u where u.email=:email and t.userId=u.id")
    Team findByEmail(@Param("email") String email);

    @Modifying
    @Query("Update Team t SET t.playerValue = (SELECT sum(marketValue) FROM Player where teamId=:teamId) where t.id=:teamId")
    void updateTeamValue(@Param("teamId") long teamId);

    Team findTopByOrderByIdDesc();

}