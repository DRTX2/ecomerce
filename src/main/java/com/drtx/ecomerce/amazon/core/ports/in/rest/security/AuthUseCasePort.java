package com.drtx.ecomerce.amazon.core.ports.in.rest.security;

import com.drtx.ecomerce.amazon.core.model.security.AuthResult;
import com.drtx.ecomerce.amazon.core.model.security.LoginCommand;
import com.drtx.ecomerce.amazon.core.model.user.User;

public interface AuthUseCasePort {
    AuthResult register(User user);

    AuthResult login(LoginCommand command);

    void logout(String token);
}
