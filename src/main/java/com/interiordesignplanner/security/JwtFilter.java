package com.interiordesignplanner.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;

/**
 * JwtFilter to validate tokens
 * 
 * Every request, the JwtToken is in the header and then validated.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final ApplicationUserDetailsService applicationUserDetailsService;

    @Value("${jwt.header}")
    private String header;

    public JwtFilter(JwtService jwtService, ApplicationUserDetailsService applicationUserDetailsService) {

        this.jwtService = jwtService;
        this.applicationUserDetailsService = applicationUserDetailsService;

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        String authHeader = request.getHeader(header);
        String token = null;
        String username = null;

        // Removes "Bearer" from the Header
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            username = jwtService.extractUsername(token);
        } else {
            filterChain.doFilter(request, response);
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            ApplicationUserDetails applicationUserDetails = applicationUserDetailsService
                    .loadUserByUsername(username);

            // If token is valid: extracts the username and finds the current user in the db
            // If not valid: it rejects the request
            if (jwtService.isTokenValid(token, applicationUserDetails)) {

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        applicationUserDetails,
                        null, applicationUserDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);

            }

        }
        filterChain.doFilter(request, response);
    }

}
