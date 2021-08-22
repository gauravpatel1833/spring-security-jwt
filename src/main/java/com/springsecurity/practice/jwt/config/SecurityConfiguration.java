package com.springsecurity.practice.jwt.config;

import com.springsecurity.practice.jwt.filter.JwtFilter;
import com.springsecurity.practice.jwt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtFilter jwtFilter;

    /*This method is to have default spring security where we can authenticate user based on it's credentials without JWT
    * so you can have a db with user and password and it can verify the details from there.
    * WebSecurityConfigurerAdapter configure method provides the AuthenticationManagerBuilder so you can define your
    * custom user service*/
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }

    /*This is overridden to provide bean injection in Login API and login api can authenticate username and password
    * using spring security based on details retrieved in userservice and generates a JWT token*/
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /*1.By default spring will pass each request for authentication but to generate jwt token
    * we need to by pass token generation url i.e /login so that the request to generate jwt token should not be authenticated
    * 2.By default spring security put the authentication in session management but we want to make it stateless for jwt.
    * sessionManagement and sessionCreationPolicy(SessionCreationPolicy.STATELESS); will make it stateless
    * 3. Adding jwtfilter before to the chaining than userpassword authentication filter
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .authorizeRequests()
                .antMatchers("/login")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    }
}
