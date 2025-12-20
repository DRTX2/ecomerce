package com.drtx.ecomerce.amazon.adapters.in.rest.incidence.dto;

import com.drtx.ecomerce.amazon.core.model.IncidenceDecision;
import jakarta.validation.constraints.NotNull;

public record ResolveIncidenceRequest(
    @NotNull IncidenceDecision decision,
    String moderatorComment
) {}
