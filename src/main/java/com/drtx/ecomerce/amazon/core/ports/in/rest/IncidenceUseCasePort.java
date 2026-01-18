package com.drtx.ecomerce.amazon.core.ports.in.rest;

import com.drtx.ecomerce.amazon.core.model.issues.Incidence;
import com.drtx.ecomerce.amazon.core.model.issues.IncidenceDecision;
import com.drtx.ecomerce.amazon.core.model.issues.Report;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IncidenceUseCasePort {
    Incidence createIncidence(Long productId, Report report, String reporterEmail);

    Optional<Incidence> getIncidenceById(Long id);
    Optional<Incidence> getIncidenceByUuid(UUID uuid);

    List<Incidence> getAllIncidences();

    Incidence resolveIncidence(Long id, IncidenceDecision decision, String moderatorComment, String moderatorEmail);
    Incidence resolveIncidenceByUuid(UUID uuid, IncidenceDecision decision, String moderatorComment, String moderatorEmail);

    Incidence updateIncidence(Long id, Incidence incidence);
    Incidence updateIncidenceByUuid(UUID uuid, Incidence incidence);
}
