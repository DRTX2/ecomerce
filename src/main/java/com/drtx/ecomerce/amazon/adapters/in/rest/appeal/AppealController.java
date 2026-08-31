package com.drtx.ecomerce.amazon.adapters.in.rest.appeal;

import com.drtx.ecomerce.amazon.adapters.in.rest.appeal.dto.AppealRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.appeal.dto.AppealResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.appeal.dto.ResolveAppealRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.appeal.mappers.AppealRestMapper;
import com.drtx.ecomerce.amazon.core.model.issues.Appeal;
import com.drtx.ecomerce.amazon.core.ports.in.rest.AppealUseCasePort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Apelaciones", description = "Sistema de apelaciones para vendedores contra resoluciones de incidencias")
@RestController
@RequestMapping("/appeals")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AppealController {

    private final AppealUseCasePort appealUseCase;
    private final AppealRestMapper mapper;

    @Operation(summary = "Crear apelación", description = "Permite a un vendedor apelar la resolución de una incidencia en su producto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Apelación creada",
                    content = @Content(schema = @Schema(implementation = AppealResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado - solo vendedores"),
            @ApiResponse(responseCode = "404", description = "Incidencia no encontrada"),
            @ApiResponse(responseCode = "400", description = "Datos de apelación inválidos"),
            @ApiResponse(responseCode = "409", description = "Ya existe apelación para esta incidencia")
    })
    @PostMapping
    public ResponseEntity<AppealResponse> createAppeal(@Valid @RequestBody AppealRequest request) {
        String sellerEmail = getAuthenticatedUserEmail();
        Appeal appeal = appealUseCase.createAppeal(request.incidenceId(), request.reason(), sellerEmail);
        return ResponseEntity.ok(mapper.toResponse(appeal));
    }

    @Operation(summary = "Obtener apelación por ID", description = "Busca una apelación específica por su identificador")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Apelación encontrada",
                    content = @Content(schema = @Schema(implementation = AppealResponse.class))),
            @ApiResponse(responseCode = "404", description = "Apelación no encontrada"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AppealResponse> getAppeal(
            @Parameter(description = "ID de la apelación", example = "1", required = true)
            @PathVariable Long id) {
        return appealUseCase.getAppealById(id)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Resolver apelación", description = "Resuelve una apelación con una decisión final. Requiere rol MODERATOR.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Apelación resuelta",
                    content = @Content(schema = @Schema(implementation = AppealResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado - requiere MODERATOR"),
            @ApiResponse(responseCode = "404", description = "Apelación no encontrada"),
            @ApiResponse(responseCode = "400", description = "Datos de resolución inválidos")
    })
    @PutMapping("/{id}/resolve")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<AppealResponse> resolveAppeal(
            @Parameter(description = "ID de la apelación", example = "1", required = true)
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
