package com.drtx.ecomerce.amazon.application.usecases.auth;

import com.drtx.ecomerce.amazon.adapters.in.security.SecurityUserDetails;
import com.drtx.ecomerce.amazon.adapters.in.security.dto.AuthRequest;
import com.drtx.ecomerce.amazon.adapters.in.security.dto.AuthResponse;
import com.drtx.ecomerce.amazon.adapters.in.security.mappers.AuthResponseMapper;
import com.drtx.ecomerce.amazon.adapters.in.security.mappers.SecurityUserMapper;
import com.drtx.ecomerce.amazon.core.model.User;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.UserRepositoryPort;
import com.drtx.ecomerce.amazon.core.ports.out.security.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private UserRepositoryPort repository;
    private PasswordService passwordService;
    private TokenProvider tokenProvider;
    private AuthenticationFacade authenticationFacade;
    private SecurityUserMapper securityUserMapper;
    private AuthResponseMapper authResponseMapper;
    private TokenRevocationPort tokenRevocationPort;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        repository = mock(UserRepositoryPort.class);
        passwordService = mock(PasswordService.class);
        tokenProvider = mock(TokenProvider.class);
        authenticationFacade = mock(AuthenticationFacade.class);
        securityUserMapper = mock(SecurityUserMapper.class);
        authResponseMapper = mock(AuthResponseMapper.class);
        tokenRevocationPort = mock(TokenRevocationPort.class);

        authService = new AuthService(repository, passwordService, tokenProvider, authenticationFacade,
                securityUserMapper, authResponseMapper, tokenRevocationPort);
    }

    @Test
    void testRegister() {
        User user = new User(null, "David", "d@test.com", "1234", "Ambato", "0999999999", null);
        when(passwordService.encode("1234")).thenReturn("encoded1234");
        when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = authService.register(user);

        assertEquals("encoded1234", result.getPassword());
        verify(passwordService).encode("1234");
        verify(repository).save(user);
    }

    @Test
    void testLogin() {
        AuthRequest request = new AuthRequest("d@test.com", "1234");
        User user = new User(1L, "David", "d@test.com", "encoded1234", "Ambato", "0999999999", null);

        // Mock de la clase concreta
        SecurityUserDetails userDetailsMock = mock(SecurityUserDetails.class);
        String token = "jwt-token";
        AuthResponse authResponse = new AuthResponse(token);

        when(repository.findByEmail("d@test.com")).thenReturn(Optional.of(user));
        doNothing().when(authenticationFacade).authenticate("d@test.com", "1234");
        when(securityUserMapper.toUserDetails(any(User.class))).thenReturn(userDetailsMock);
        when(tokenProvider.generateToken(userDetailsMock)).thenReturn(token);
        when(authResponseMapper.fromToken(token)).thenReturn(authResponse);

        AuthResponse result = authService.login(request);

        assertEquals(token, result.token());
        verify(authenticationFacade).authenticate("d@test.com", "1234");
        verify(tokenProvider).generateToken(userDetailsMock);
    }



    @Test
    void testLogout() {
        String authHeader = "Bearer jwt-token";
        doNothing().when(tokenRevocationPort).invalidate("jwt-token");

        authService.logout(authHeader);

        verify(tokenRevocationPort).invalidate("jwt-token");
    }
}
