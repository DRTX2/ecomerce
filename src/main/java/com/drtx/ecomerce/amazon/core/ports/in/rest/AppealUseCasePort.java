package com.drtx.ecomerce.amazon.core.ports.in.rest;

import com.drtx.ecomerce.amazon.core.model.issues.Appeal;
import com.drtx.ecomerce.amazon.core.model.issues.AppealDecision;

import java.util.Optional;

public interface AppealUseCasePort {
    Appeal createAppeal(Long incidenceId, String reason, String sellerEmail);

    Optional<Appeal> getAppealById(Long id);

    Appeal resolveAppeal(Long id, AppealDecision decision, String moderatorEmail);
}
