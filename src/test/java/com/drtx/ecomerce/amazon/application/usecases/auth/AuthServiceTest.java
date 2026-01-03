package com.drtx.ecomerce.amazon.application.usecases.auth;

import com.drtx.ecomerce.amazon.core.model.exceptions.DomainException;
import com.drtx.ecomerce.amazon.core.model.user.User;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.UserRepositoryPort;
import com.drtx.ecomerce.amazon.core.ports.out.security.AuthenticationFacade;
import com.drtx.ecomerce.amazon.core.ports.out.security.PasswordService;
import com.drtx.ecomerce.amazon.core.ports.out.security.TokenProvider;
import com.drtx.ecomerce.amazon.core.ports.out.security.TokenRevocationPort;
import com.drtx.ecomerce.amazon.adapters.in.security.mappers.SecurityUserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepositoryPort repository;
    @Mock
    private PasswordService passwordService;
    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private AuthenticationFacade authenticationFacade;
    @Mock
    private SecurityUserMapper securityUserMapper;
    @Mock
    private TokenRevocationPort tokenRevocationPort;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Should throw DomainException when password contains email")
    void shouldThrowExceptionWhenPasswordContainsEmail() {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("Mytest@gmail.comPassword");

        assertThrows(DomainException.class, () -> authService.register(user));
    }

    @Test
    @DisplayName("Should throw DomainException when password is equal to email with different case")
    void shouldThrowExceptionWhenPasswordEqualsEmailCaseInsensitive() {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("Test@gmail.com");

        assertThrows(DomainException.class, () -> authService.register(user));
    }
}
