package com.drtx.ecomerce.amazon.adapters.in.rest.appeal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AppealRequest(
    @NotNull UUID incidenceUuid,
    @NotBlank String reason
) {}
