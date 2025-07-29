package com.drtx.ecomerce.amazon.core.ports.out.security;

public interface  AuthenticationFacade {
    void authenticate(String username, String password);
}
