package com.springsecurity.practice.jwt.controller;

import com.springsecurity.practice.jwt.model.JwtRequest;
import com.springsecurity.practice.jwt.model.JwtResponse;
import com.springsecurity.practice.jwt.service.UserService;
import com.springsecurity.practice.jwt.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;


    /* This method is to take user and password as input and authenticate the user and generate the JWT Token
    */
    @PostMapping("/login")
    public JwtResponse login(@RequestBody JwtRequest jwtRequest) throws Exception {

        /*Below will authenticate the credentials automatically OOB against userService loadUserByUsername*/
        try{
            authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                      jwtRequest.getUsername(),
                      jwtRequest.getPassword())
            );
        }catch (BadCredentialsException e){
            throw new Exception("INVALID_CREDENTIALS",e);
        }

        //Fetch userdetails using username to generate jwt token
        final UserDetails userDetails
                = userService.loadUserByUsername(jwtRequest.getUsername());

        //Generate JWT Token
        final String token =
                jwtUtil.generateToken(userDetails);

        return new JwtResponse(token);
    }
}
