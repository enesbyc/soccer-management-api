package com.soccer.management.controller;

import java.util.Objects;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.soccer.management.dto.AuthenticationRequestDTO;
import com.soccer.management.dto.AuthenticationResponseDTO;
import com.soccer.management.dto.UserDTO;
import com.soccer.management.exception.ValidationException;
import com.soccer.management.response.ResponseBuilder;
import com.soccer.management.security.JwtUtil;
import com.soccer.management.service.impl.UserService;

import io.swagger.annotations.ApiOperation;

/**
 * @author enes.boyaci
 */
@RestController
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    @ApiOperation(value = "Authenticate user",
                  notes = "Authenticate user with given email and password credentials. If user successfull authenticated, JWT token will generated.")
    public ResponseEntity<AuthenticationResponseDTO> authenticate(@Valid @RequestBody AuthenticationRequestDTO authenticationRequest) throws Exception {
        try {
            if (Objects.isNull(authenticationRequest)
                || Objects.isNull(authenticationRequest.getEmail())
                || authenticationRequest.getEmail().equals(""))
                throw new ValidationException("Email cannot be empty!");

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getEmail(), authenticationRequest.getPassword()));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        }

        UserDetails userdetails = userService.loadUserByUsername(authenticationRequest.getEmail());
        String token = jwtUtil.generateToken(userdetails);
        return ResponseBuilder.build(new AuthenticationResponseDTO(token));
    }

    @ApiOperation(value = "Register user",
                  notes = "Register user with given email and password credentials. After registration success, creates team and add 20 player to team for this user.")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<UserDTO> register(@Valid @RequestBody UserDTO user) throws Exception {
        return ResponseBuilder.build(userService.save(user), HttpStatus.CREATED);
    }
}
