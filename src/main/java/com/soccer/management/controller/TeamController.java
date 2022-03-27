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

import com.soccer.management.dto.TeamDTO;
import com.soccer.management.response.ResponseBuilder;
import com.soccer.management.service.ITeamService;

import io.swagger.annotations.ApiOperation;

/**
 * @author enes.boyaci
 */
@RestController
public class TeamController {

    @Autowired
    private ITeamService teamService;

    @ApiOperation(value = "Create team", notes = "Creates team with given information.")
    @RequestMapping(value = "/team", method = RequestMethod.POST)
    public ResponseEntity<TeamDTO> create(@Valid @RequestBody TeamDTO team) {
        return ResponseBuilder.build(teamService.save(team), HttpStatus.CREATED);
    }

    @ApiOperation(value = "Delete team", notes = "Delete team with given team id.")
    @RequestMapping(value = "/team/{teamId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@Valid @NotNull @PathVariable("teamId") Long teamId) {
        teamService.delete(teamId);
        return ResponseBuilder.build(HttpStatus.OK);
    }

    @ApiOperation(value = "Update team", notes = "Update team with given information.")
    @RequestMapping(value = "/team", method = RequestMethod.PATCH)
    public ResponseEntity<TeamDTO> update(@Valid @RequestBody TeamDTO team) {
        return ResponseBuilder.build(teamService.update(team), HttpStatus.OK);
    }

    @ApiOperation(value = "Get team.", notes = "Get team list.")
    @RequestMapping(value = "/team", method = RequestMethod.GET)
    public ResponseEntity<List<TeamDTO>> get() {
        return ResponseBuilder.build(teamService.get(), HttpStatus.OK);
    }

}
