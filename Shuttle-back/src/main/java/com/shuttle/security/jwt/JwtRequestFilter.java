package com.shuttle.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private UserDetailsService userDetailsService;

    private JwtTokenUtil tokenUtil;

    protected final Log LOGGER = LogFactory.getLog(getClass());

    public JwtRequestFilter(JwtTokenUtil tokenHelper, UserDetailsService userDetailsService) {
        this.tokenUtil = tokenHelper;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {


        String username;


        String authToken = tokenUtil.getToken(request);

        try {

            if (authToken != null) {

                username = tokenUtil.getEmailFromToken(authToken);

                if (username != null) {


                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    if (tokenUtil.validateToken(authToken, userDetails)) {
                        TokenBasedAuthentication authentication = new TokenBasedAuthentication(userDetails);
                        authentication.setToken(authToken);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }

        } catch (ExpiredJwtException ex) {
            String isRefreshToken = request.getHeader("isRefreshToken");
            String requestURL = request.getRequestURL().toString();
            // allow for Refresh Token creation if following conditions are true.
            if (isRefreshToken != null && isRefreshToken.equals("true") && requestURL.contains("refreshtoken")) {
                allowForRefreshToken(ex, request,authToken);
            }

            chain.doFilter(request, response);
        }
    }
        private void allowForRefreshToken(ExpiredJwtException ex, HttpServletRequest request,String authToken) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(tokenUtil.getEmailFromToken(authToken));
            // create a UsernamePasswordAuthenticationToken with null values.
            TokenBasedAuthentication authentication = new TokenBasedAuthentication(userDetails);
            authentication.setToken(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // Set the claims so that in controller we will be using it to create
            // new JWT
            request.setAttribute("claims", ex.getClaims());

        }
}
