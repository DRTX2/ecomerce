package com.drtx.ecomerce.amazon.adapters.in.rest.appeal;

import com.drtx.ecomerce.amazon.adapters.in.rest.appeal.dto.AppealRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.appeal.dto.AppealResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.appeal.dto.ResolveAppealRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.appeal.mappers.AppealRestMapper;
import com.drtx.ecomerce.amazon.core.model.issues.Appeal;
import com.drtx.ecomerce.amazon.core.ports.in.rest.AppealUseCasePort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/appeals")
@RequiredArgsConstructor
public class AppealController {

    private final AppealUseCasePort appealUseCase;
    private final AppealRestMapper mapper;

    @PostMapping
    public ResponseEntity<AppealResponse> createAppeal(@Valid @RequestBody AppealRequest request) {
        String sellerEmail = getAuthenticatedUserEmail();
        Appeal appeal = appealUseCase.createAppeal(request.incidenceId(), request.reason(), sellerEmail);
        return ResponseEntity.ok(mapper.toResponse(appeal));
    }

    /**
     * Get appeal by UUID (main endpoint)
     */
    @GetMapping("/{uuid}")
    public ResponseEntity<AppealResponse> getAppealByUuid(@PathVariable UUID uuid) {
        return appealUseCase.getAppealByUuid(uuid)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Resolve appeal by UUID (main endpoint - MODERATOR only)
     */
    @PutMapping("/{uuid}/resolve")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<AppealResponse> resolveAppealByUuid(
            @PathVariable UUID uuid,
            @Valid @RequestBody ResolveAppealRequest request) {
        String moderatorEmail = getAuthenticatedUserEmail();
        Appeal appeal = appealUseCase.resolveAppealByUuid(uuid, request.decision(), moderatorEmail);
        return ResponseEntity.ok(mapper.toResponse(appeal));
    }

    // Legacy endpoints using Long ID (deprecated)

    /**
     * @deprecated Use {@link #getAppealByUuid(UUID)} instead
     */
    @Deprecated
    @GetMapping("/by-id/{id}")
    public ResponseEntity<AppealResponse> getAppeal(@PathVariable Long id) {
        return appealUseCase.getAppealById(id)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * @deprecated Use {@link #resolveAppealByUuid(UUID, ResolveAppealRequest)} instead
     */
    @Deprecated
    @PutMapping("/by-id/{id}/resolve")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<AppealResponse> resolveAppeal(
            @PathVariable Long id,
            @Valid @RequestBody ResolveAppealRequest request) {
        String moderatorEmail = getAuthenticatedUserEmail();
        Appeal appeal = appealUseCase.resolveAppeal(id, request.decision(), moderatorEmail);
        return ResponseEntity.ok(mapper.toResponse(appeal));
    }

    private String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !authentication.getPrincipal().equals("anonymousUser")) {
            return authentication.getName();
        }
        return null;
    }
}
