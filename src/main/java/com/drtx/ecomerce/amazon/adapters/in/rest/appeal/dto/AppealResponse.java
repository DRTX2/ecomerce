package com.drtx.ecomerce.amazon.adapters.in.rest.appeal.dto;

import com.drtx.ecomerce.amazon.adapters.in.rest.incidence.dto.IncidenceResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.user.dto.UserResponse;
import com.drtx.ecomerce.amazon.core.model.AppealDecision;
import com.drtx.ecomerce.amazon.core.model.AppealStatus;

import java.time.LocalDateTime;

public record AppealResponse(
    Long id,
    IncidenceResponse incidence,
    UserResponse seller,
    String reason,
    LocalDateTime createdAt,
    AppealStatus status,
    UserResponse newModerator,
    AppealDecision finalDecision,
    LocalDateTime finalDecisionAt
) {}
