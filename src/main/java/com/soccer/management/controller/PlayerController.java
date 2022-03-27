package com.soccer.management.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.soccer.management.dto.PlayerDTO;
import com.soccer.management.response.ResponseBuilder;
import com.soccer.management.service.IPlayerService;

import io.swagger.annotations.ApiOperation;

/**
 * @author enes.boyaci
 */
@RestController
public class PlayerController {

    @Autowired
    private IPlayerService playerService;

    @ApiOperation(value = "Create player", notes = "Create player with given information.")
    @RequestMapping(value = "/player", method = RequestMethod.POST)
    public ResponseEntity<PlayerDTO> create(@Valid @RequestBody PlayerDTO playerDTO) {
        return ResponseBuilder.build(playerService.save(playerDTO), HttpStatus.CREATED);
    }

    @ApiOperation(value = "Delete player", notes = "Delete player with given player id.")
    @RequestMapping(value = "/player/{playerId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@Valid @NotNull @PathVariable("playerId") Long playerId) {
        playerService.delete(playerId);
        return ResponseBuilder.build(HttpStatus.OK);
    }

    @ApiOperation(value = "Update player", notes = "Update player with given information.")
    @RequestMapping(value = "/player", method = RequestMethod.PATCH)
    public ResponseEntity<PlayerDTO> update(@Valid @RequestBody PlayerDTO playerDTO) {
        return ResponseBuilder.build(playerService.update(playerDTO), HttpStatus.OK);
    }

    @ApiOperation(value = "Get player list.", notes = "Get player list.")
    @RequestMapping(value = "/player", method = RequestMethod.GET)
    public ResponseEntity<List<PlayerDTO>> get() {
        return ResponseBuilder.build(playerService.get(), HttpStatus.OK);
    }

}
