package com.drtx.ecomerce.amazon.adapters.in.rest.incidence.dto;

import com.drtx.ecomerce.amazon.adapters.in.rest.product.dto.ProductResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.user.dto.UserResponse;
import com.drtx.ecomerce.amazon.core.model.IncidenceDecision;
import com.drtx.ecomerce.amazon.core.model.IncidenceStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record IncidenceResponse(
    Long id,
    UUID publicUi,
    ProductResponse product,
    IncidenceStatus status,
    LocalDateTime createdAt,
    Boolean autoclosed,
    UserResponse moderator,
    String moderatorComment,
    IncidenceDecision decision,
    List<ReportResponse> reports
) {}
