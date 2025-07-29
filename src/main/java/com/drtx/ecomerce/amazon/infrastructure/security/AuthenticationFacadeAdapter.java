package com.drtx.ecomerce.amazon.infrastructure.security;

import com.drtx.ecomerce.amazon.core.ports.out.security.AuthenticationFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationFacadeAdapter implements AuthenticationFacade {
    private final AuthenticationManager authenticationManager;


    @Override
    public void authenticate(String email, String password) {
        var authToken=new UsernamePasswordAuthenticationToken(email,password);
        authenticationManager.authenticate(authToken);
    }
}
