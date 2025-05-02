package com.drtx.ecomerce.amazon.adapters.in.rest.user.dto;

import jakarta.annotation.Nullable;

import java.util.Date;

public record UserResponse (
        Long id,
        String name,
        String email,
        String role,
        String address,
        String phone
){
}
