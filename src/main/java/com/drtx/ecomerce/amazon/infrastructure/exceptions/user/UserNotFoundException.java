package com.drtx.ecomerce.amazon.infrastructure.exceptions.user;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(Long id){
        super("User not found with id " + id);
    }

    public UserNotFoundException(UUID uuid){
        super("User not found with UUID " + uuid);
    }
}
