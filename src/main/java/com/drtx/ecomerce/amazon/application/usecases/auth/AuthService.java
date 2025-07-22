package com.drtx.ecomerce.amazon.application.usecases.auth;

import com.drtx.ecomerce.amazon.adapters.in.security.dto.AuthRequest;
import com.drtx.ecomerce.amazon.adapters.in.security.dto.AuthResponse;
import com.drtx.ecomerce.amazon.adapters.in.security.dto.RegisterRequest;
import com.drtx.ecomerce.amazon.adapters.in.security.mappers.AuthResponseMapper;
import com.drtx.ecomerce.amazon.adapters.in.security.mappers.SecurityUserMapper;
import com.drtx.ecomerce.amazon.adapters.in.security.mappers.UserSecurityMapper;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.UserRepositoryPort;
import com.drtx.ecomerce.amazon.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepositoryPort repository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserSecurityMapper securityMapper;
    private final AuthResponseMapper authResponseMapper;
    private final SecurityUserMapper securityUserMapper;

    public AuthResponse register(RegisterRequest request) {
        var user = securityMapper.registerRequestToDomain(request);
        user.setPassword(encoder.encode(user.getPassword()));
        UserDetails savedUser=securityUserMapper.toUserDetails(repository.save(user));
        var jwtToken = jwtService.generateToken(savedUser);
        return authResponseMapper.fromToken(jwtToken);
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        encoder.encode(request.password())
                )
        );
        var user = repository.findByEmail(request.email())
                .orElseThrow(()-> new UsernameNotFoundException("User not found with email: " + request.email()));
        UserDetails userDetails= securityUserMapper.toUserDetails(user);
        var jwtToken = jwtService.generateToken(userDetails);
        return authResponseMapper.fromToken(jwtToken);
    }
}