package com.drtx.ecomerce.amazon.adapters.in.rest.appeal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AppealRequest(
    @NotNull Long incidenceId,
    @NotBlank String reason
) {}
