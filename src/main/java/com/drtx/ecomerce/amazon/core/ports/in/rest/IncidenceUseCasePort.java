package com.drtx.ecomerce.amazon.core.ports.in.rest;

import com.drtx.ecomerce.amazon.core.model.issues.Incidence;
import com.drtx.ecomerce.amazon.core.model.issues.IncidenceDecision;
import com.drtx.ecomerce.amazon.core.model.issues.Report;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IncidenceUseCasePort {
    Incidence createIncidence(UUID productUuid, Report report, String reporterEmail);

    Optional<Incidence> getIncidenceByUuid(UUID uuid);

    List<Incidence> getAllIncidences();

    Incidence resolveIncidenceByUuid(UUID uuid, IncidenceDecision decision, String moderatorComment, String moderatorEmail);

    Incidence updateIncidenceByUuid(UUID uuid, Incidence incidence);

    void deleteIncidenceByUuid(UUID uuid);

    // Legacy support (Deprecated)
    @Deprecated
    Optional<Incidence> getIncidenceById(Long id);
    @Deprecated
    Incidence createIncidence(Long productId, Report report, String reporterEmail);
    @Deprecated
    Incidence resolveIncidence(Long id, IncidenceDecision decision, String moderatorComment, String moderatorEmail);
}
