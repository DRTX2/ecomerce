package com.drtx.ecomerce.amazon.core.ports.in.rest;

import com.drtx.ecomerce.amazon.core.model.issues.Appeal;
import com.drtx.ecomerce.amazon.core.model.issues.AppealDecision;

import java.util.Optional;
import java.util.UUID;

public interface AppealUseCasePort {
    Appeal createAppeal(Long incidenceId, String reason, String sellerEmail);

    Optional<Appeal> getAppealById(Long id);
    Optional<Appeal> getAppealByUuid(UUID uuid);

    Appeal resolveAppeal(Long id, AppealDecision decision, String moderatorEmail);
    Appeal resolveAppealByUuid(UUID uuid, AppealDecision decision, String moderatorEmail);
}
