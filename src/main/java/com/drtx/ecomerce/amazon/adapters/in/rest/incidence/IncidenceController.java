package com.drtx.ecomerce.amazon.adapters.in.rest.incidence;

import com.drtx.ecomerce.amazon.adapters.in.rest.incidence.dto.IncidenceResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.incidence.dto.ReportRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.incidence.dto.ResolveIncidenceRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.incidence.mappers.IncidenceRestMapper;
import com.drtx.ecomerce.amazon.core.model.issues.Incidence;
import com.drtx.ecomerce.amazon.core.model.issues.Report;
import com.drtx.ecomerce.amazon.core.ports.in.rest.IncidenceUseCasePort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/incidences")
@RequiredArgsConstructor
public class IncidenceController {

    private final IncidenceUseCasePort incidenceUseCase;
    private final IncidenceRestMapper mapper;

    @PostMapping("/product/{productUuid}")
    public ResponseEntity<IncidenceResponse> reportProduct(
            @PathVariable UUID productUuid,
            @Valid @RequestBody ReportRequest request
    ) {
        String userEmail = getAuthenticatedUserEmail();
        Report report = mapper.toDomain(request);
        Incidence incidence = incidenceUseCase.createIncidence(productUuid, report, userEmail);
        return ResponseEntity.ok(mapper.toResponse(incidence));
    }

    @Deprecated
    @PostMapping("/product/legacy/{productId}")
    public ResponseEntity<IncidenceResponse> reportProductLegacy(
            @PathVariable Long productId,
            @Valid @RequestBody ReportRequest request
    ) {
        String userEmail = getAuthenticatedUserEmail();
        Report report = mapper.toDomain(request);
        Incidence incidence = incidenceUseCase.createIncidence(productId, report, userEmail);
        return ResponseEntity.ok(mapper.toResponse(incidence));
    }

    @GetMapping
    public ResponseEntity<List<IncidenceResponse>> getAllIncidences() {
        return ResponseEntity.ok(
                incidenceUseCase.getAllIncidences().stream()
                        .map(mapper::toResponse)
                        .collect(Collectors.toList())
        );
    }

    /**
     * Get incidence by UUID (main endpoint)
     */
    @GetMapping("/{uuid}")
    public ResponseEntity<IncidenceResponse> getIncidenceByUuid(@PathVariable UUID uuid) {
        return incidenceUseCase.getIncidenceByUuid(uuid)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Resolve incidence by UUID (main endpoint - MODERATOR only)
     */
    @PutMapping("/{uuid}/resolve")
    public ResponseEntity<IncidenceResponse> resolveIncidenceByUuid(
            @PathVariable UUID uuid,
            @Valid @RequestBody ResolveIncidenceRequest request
    ) {
        String moderatorEmail = getAuthenticatedUserEmail();
        Incidence incidence = incidenceUseCase.resolveIncidenceByUuid(
                uuid,
                request.decision(),
                request.moderatorComment(),
                moderatorEmail
        );
        return ResponseEntity.ok(mapper.toResponse(incidence));
    }

    /**
     * Delete incidence by UUID (main endpoint - MODERATOR only)
     */
    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteIncidenceByUuid(@PathVariable UUID uuid) {
        incidenceUseCase.deleteIncidenceByUuid(uuid);
        return ResponseEntity.noContent().build();
    }

    // Legacy endpoints using Long ID (deprecated)

    /**
     * @deprecated Use {@link #getIncidenceByUuid(UUID)} instead
     */
    @Deprecated
    @GetMapping("/by-id/{id}")
    public ResponseEntity<IncidenceResponse> getIncidence(@PathVariable Long id) {
        return incidenceUseCase.getIncidenceById(id)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * @deprecated Use {@link #resolveIncidenceByUuid(UUID, ResolveIncidenceRequest)} instead
     */
    @Deprecated
    @PutMapping("/by-id/{id}/resolve")
    public ResponseEntity<IncidenceResponse> resolveIncidence(
            @PathVariable Long id,
            @Valid @RequestBody ResolveIncidenceRequest request
    ) {
        String moderatorEmail = getAuthenticatedUserEmail();
        Incidence incidence = incidenceUseCase.resolveIncidence(
                id,
                request.decision(),
                request.moderatorComment(),
                moderatorEmail
        );
        return ResponseEntity.ok(mapper.toResponse(incidence));
    }

    private String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
             // Assuming Principal name is the email/username
             return authentication.getName();
        }
        return null;
    }
}
