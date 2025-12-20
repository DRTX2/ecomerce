package com.drtx.ecomerce.amazon.adapters.in.rest.incidence.dto;

import com.drtx.ecomerce.amazon.adapters.in.rest.user.dto.UserResponse;
import com.drtx.ecomerce.amazon.core.model.ReportSource;

import java.time.LocalDateTime;

public record ReportResponse(
    Long id,
    String reason,
    String comment,
    UserResponse reporter,
    LocalDateTime createdAt,
    ReportSource source
) {}
