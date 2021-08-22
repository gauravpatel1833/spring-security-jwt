package com.springsecurity.practice.jwt.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserService implements UserDetailsService {

    /*This is custom user service which implements spring user detail service
    * the override method loadUserByUsername we get the username from the request
    * either by default spring security page or from the request then we can query our user db to get those user details
    * here we are hardcoding the user details for demo purpose*/
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

        //Logic to get user details from the database
        //Here authorities are passed as blank arraylist but we can add roles per user which are authorized
        return new User("admin","password",new ArrayList<>());
    }
}
