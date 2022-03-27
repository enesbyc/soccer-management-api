package com.soccer.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.soccer.management.model.Transfer;

/**
 * @author enes.boyaci
 */
@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    Transfer findByPlayerId(Long playerId);

    void deleteByPlayerId(long playerId);

}