package com.drtx.ecomerce.amazon.core.ports.out.security;

public interface PasswordService {
    String encode(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}
