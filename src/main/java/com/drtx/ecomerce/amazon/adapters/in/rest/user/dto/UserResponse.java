package com.drtx.ecomerce.amazon.adapters.in.rest.user.dto;

import jakarta.annotation.Nullable;

import java.util.Date;
import java.util.UUID;

public record UserResponse (
        UUID uuid,
        String name,
        String email,
        String role,
        String address,
        String phone
){
}
