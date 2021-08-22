package com.springsecurity.practice.jwt.filter;

import com.springsecurity.practice.jwt.service.UserService;
import com.springsecurity.practice.jwt.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    /*For each request filter will be executed to extract jwt token from header bearer and based on token
            it will extract the user details and validate the token and add it to the security context*/
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String authorization = httpServletRequest.getHeader("Authorization");
        String token = null;
        String userName = null;

        //Extract the token and fetch user name
        if(null != authorization && authorization.startsWith("Bearer")){
            token = authorization.substring(7);
            userName = jwtUtil.getUsernameFromToken(token);
        }

        //If user is not null and security context is empty then validate token and add the authorities to security context
        if(userName !=null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails=
                    userService.loadUserByUsername(userName);

            if(jwtUtil.validateToken(token,userDetails)){
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails,
                                null, userDetails.getAuthorities());

                usernamePasswordAuthenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(httpServletRequest)
                );

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

            }
        }
        //Continue the filter chaining
        filterChain.doFilter(httpServletRequest,httpServletResponse);
    }
}
