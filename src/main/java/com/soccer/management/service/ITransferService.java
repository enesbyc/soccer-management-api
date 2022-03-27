package com.soccer.management.service;

import java.util.List;

import com.soccer.management.dto.PlayerDTO;
import com.soccer.management.dto.TransferDTO;
import com.soccer.management.dto.TransferFilterDTO;

/**
 * @author enes.boyaci
 */
public interface ITransferService {

    TransferDTO transferPlayer(TransferDTO transferDTO);

    PlayerDTO buyPlayer(Long playerId);

    void delete(long id);

    List<TransferDTO> get(TransferFilterDTO transferFilterDTO);

}
