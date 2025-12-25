package com.drtx.ecomerce.amazon.adapters.in.rest.appeal.dto;

import com.drtx.ecomerce.amazon.core.model.issues.AppealDecision;
import jakarta.validation.constraints.NotNull;

public record ResolveAppealRequest(
        @NotNull AppealDecision decision) {
}
