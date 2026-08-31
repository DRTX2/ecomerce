package com.drtx.ecomerce.amazon.adapters.in.rest.incidence;

import com.drtx.ecomerce.amazon.adapters.in.rest.incidence.dto.IncidenceResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.incidence.dto.ReportRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.incidence.dto.ResolveIncidenceRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.incidence.mappers.IncidenceRestMapper;
import com.drtx.ecomerce.amazon.core.model.issues.Incidence;
import com.drtx.ecomerce.amazon.core.model.issues.Report;
import com.drtx.ecomerce.amazon.core.ports.in.rest.IncidenceUseCasePort;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Incidencias", description = "Sistema de reporte y gestión de incidencias de productos")
@RestController
@RequestMapping("/incidences")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class IncidenceController {

    private final IncidenceUseCasePort incidenceUseCase;
    private final IncidenceRestMapper mapper;

    @Operation(summary = "Reportar incidencia en producto", description = "Crea un reporte de incidencia para un producto específico por el usuario autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Incidencia creada",
                    content = @Content(schema = @Schema(implementation = IncidenceResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos de reporte inválidos")
    })
    @PostMapping("/product/{productId}")
    public ResponseEntity<IncidenceResponse> reportProduct(
            @Parameter(description = "ID del producto", example = "1", required = true)
            @PathVariable Long productId,
            @Valid @RequestBody ReportRequest request
    ) {
        String userEmail = getAuthenticatedUserEmail();
        Report report = mapper.toDomain(request);
        Incidence incidence = incidenceUseCase.createIncidence(productId, report, userEmail);
        return ResponseEntity.ok(mapper.toResponse(incidence));
    }

    @Operation(summary = "Listar todas las incidencias", description = "Obtiene todas las incidencias (requiere rol MODERATOR o ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de incidencias",
                    content = @Content(schema = @Schema(implementation = IncidenceResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping
    public ResponseEntity<List<IncidenceResponse>> getAllIncidences() {
        return ResponseEntity.ok(
                incidenceUseCase.getAllIncidences().stream()
                        .map(mapper::toResponse)
                        .collect(Collectors.toList())
        );
    }

    @Operation(summary = "Obtener incidencia por ID", description = "Busca una incidencia específica por su identificador")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Incidencia encontrada",
                    content = @Content(schema = @Schema(implementation = IncidenceResponse.class))),
            @ApiResponse(responseCode = "404", description = "Incidencia no encontrada"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<IncidenceResponse> getIncidence(
            @Parameter(description = "ID de la incidencia", example = "1", required = true)
            @PathVariable Long id) {
        return incidenceUseCase.getIncidenceById(id)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Resolver incidencia", description = "Resuelve una incidencia con una decisión. Requiere rol MODERATOR o ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Incidencia resuelta",
                    content = @Content(schema = @Schema(implementation = IncidenceResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado - requiere MODERATOR/ADMIN"),
            @ApiResponse(responseCode = "404", description = "Incidencia no encontrada"),
            @ApiResponse(responseCode = "400", description = "Datos de resolución inválidos")
    })
    @PutMapping("/{id}/resolve")
    public ResponseEntity<IncidenceResponse> resolveIncidence(
            @Parameter(description = "ID de la incidencia", example = "1", required = true)
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
