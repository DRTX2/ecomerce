package com.drtx.ecomerce.amazon.adapters.in.security;

import com.drtx.ecomerce.amazon.core.ports.out.security.TokenProvider;
import com.drtx.ecomerce.amazon.core.ports.out.security.TokenRevocationPort;
import com.drtx.ecomerce.amazon.infrastructure.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final TokenRevocationPort tokenRevocationPort;
    private final UserDetailsService userDetailsService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();

        System.out.println("JwtAuthFilter - Request URI: " + path);
        System.out.println("JwtAuthFilter - Context Path: " + contextPath);

        // Remove context path to get the actual endpoint path
        String endpoint = path;
        if (contextPath != null && !contextPath.isEmpty() && path.startsWith(contextPath)) {
            endpoint = path.substring(contextPath.length());
        }

        System.out.println("JwtAuthFilter - Endpoint: " + endpoint);

        boolean skip = endpoint.equals("/auth/login") ||
                       endpoint.equals("/auth/register") ||
                       endpoint.startsWith("/auth/");
        System.out.println("JwtAuthFilter - Should skip: " + skip);
        return skip;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token;
        final String username;


        token = authHeader.substring(7);
        username = tokenProvider.extractUsername(token);

        if(tokenRevocationPort.isInvalidated(token)){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authToken.setDetails(// to get more details, pe ip, connection port, browser....
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}
