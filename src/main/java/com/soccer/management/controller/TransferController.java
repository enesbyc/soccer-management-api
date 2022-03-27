package com.soccer.management.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.soccer.management.dto.PlayerDTO;
import com.soccer.management.dto.TransferDTO;
import com.soccer.management.dto.TransferFilterDTO;
import com.soccer.management.response.ResponseBuilder;
import com.soccer.management.service.ITransferService;

import io.swagger.annotations.ApiOperation;

/**
 * @author enes.boyaci
 */
@RestController
public class TransferController {

    @Autowired
    private ITransferService transferService;

    @ApiOperation(value = "Transfer player",
                  notes = "Initiates the player's transfer process. When the transfer process starts, the player will be shown in the transfer list.")
    @RequestMapping(value = "/transfer", method = RequestMethod.POST)
    public ResponseEntity<TransferDTO> transfer(@Valid @RequestBody TransferDTO transfer) {
        return ResponseBuilder.build(transferService.transferPlayer(transfer), HttpStatus.CREATED);
    }

    @ApiOperation(value = "Buy player", notes = "Buy player and add to own team.")
    @RequestMapping(value = "/transfer/buy/{playerId}", method = RequestMethod.POST)
    public ResponseEntity<PlayerDTO> buyPlayer(@Valid @NotNull @PathVariable("playerId") Long playerId) {
        return ResponseBuilder.build(transferService.buyPlayer(playerId), HttpStatus.OK);
    }

    @ApiOperation(value = "Delete transfer", notes = "Delete player on transfer list.")
    @RequestMapping(value = "/transfer/{playerId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@Valid @NotNull @PathVariable("playerId") Long playerId) {
        transferService.delete(playerId);
        return ResponseBuilder.build(HttpStatus.OK);
    }

    @ApiOperation(value = "Get player list on transfer.", notes = "")
    @RequestMapping(value = "/transfer", method = RequestMethod.GET)
    public ResponseEntity<List<TransferDTO>> getTransferList(@Param("country") String country,
                                                             @Param("playerName") String playerName,
                                                             @Param("playerValue") String playerValue,
                                                             @Param("teamName") String teamName) {

        return ResponseBuilder.build(transferService
                        .get(TransferFilterDTO.builder().country(country).playerName(playerName)
                                        .playerValue(playerValue).teamName(teamName).build()),
                                     HttpStatus.OK);
    }

}
