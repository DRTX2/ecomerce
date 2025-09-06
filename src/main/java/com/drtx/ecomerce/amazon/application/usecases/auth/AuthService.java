package com.drtx.ecomerce.amazon.application.usecases.auth;

import com.drtx.ecomerce.amazon.adapters.in.security.dto.AuthRequest;
import com.drtx.ecomerce.amazon.adapters.in.security.dto.AuthResponse;
import com.drtx.ecomerce.amazon.adapters.in.security.mappers.AuthResponseMapper;
import com.drtx.ecomerce.amazon.adapters.in.security.mappers.SecurityUserMapper;
import com.drtx.ecomerce.amazon.core.model.User;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.UserRepositoryPort;
import com.drtx.ecomerce.amazon.core.ports.out.security.AuthenticationFacade;
import com.drtx.ecomerce.amazon.core.ports.out.security.PasswordService;
import com.drtx.ecomerce.amazon.core.ports.out.security.TokenProvider;
import com.drtx.ecomerce.amazon.core.ports.out.security.TokenRevocationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepositoryPort repository;
    private final PasswordService passwordService;
    private final TokenProvider tokenProvider;
    private final AuthenticationFacade authenticationFacade;
    private final SecurityUserMapper securityUserMapper;
    private final AuthResponseMapper authResponseMapper;
    private final TokenRevocationPort tokenRevocationPort;

    // se q son tipos distintos, luego usare un mapper
    public User register(User user){
        String encodedPassword=passwordService.encode(user.getPassword());
        user.setPassword(encodedPassword);
        return repository.save(user);

    }

    // me parece mejor dejarlo con nombres clave,
    public AuthResponse login(AuthRequest request) {
        authenticationFacade.authenticate(request.email(), request.password());

        var user = repository.findByEmail(request.email())
                .orElseThrow(
                        ()-> new UsernameNotFoundException("User not found with email: " + request.email())
                );
        var userDetails= securityUserMapper.toUserDetails(user);
        var jwt = tokenProvider.generateToken(userDetails);
        return authResponseMapper.fromToken(jwt);
    }

    public void logout(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        tokenRevocationPort.invalidate(token);
    }
}