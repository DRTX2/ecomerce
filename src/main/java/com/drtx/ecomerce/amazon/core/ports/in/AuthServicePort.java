package com.drtx.ecomerce.amazon.core.ports.in;

import com.drtx.ecomerce.amazon.core.model.User;

public interface AuthServicePort {
    String login(String email, String password);
    void register(User user);
}
