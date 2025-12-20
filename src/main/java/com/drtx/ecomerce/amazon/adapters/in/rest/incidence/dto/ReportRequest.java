package com.drtx.ecomerce.amazon.adapters.in.rest.incidence.dto;

import jakarta.validation.constraints.NotBlank;

public record ReportRequest(
    @NotBlank String reason,
    String comment
) {}
