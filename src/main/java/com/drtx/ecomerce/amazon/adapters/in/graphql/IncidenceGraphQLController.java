package com.drtx.ecomerce.amazon.adapters.in.graphql;

import com.drtx.ecomerce.amazon.core.model.issues.Incidence;
import com.drtx.ecomerce.amazon.core.model.issues.IncidenceDecision;
import com.drtx.ecomerce.amazon.core.model.issues.Report;
import com.drtx.ecomerce.amazon.core.ports.in.rest.IncidenceUseCasePort;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class IncidenceGraphQLController {

    private final IncidenceUseCasePort incidenceUseCase;

    @SchemaMapping(typeName = "Query", field = "getAllIncidences")
    @PreAuthorize("hasRole('MODERATOR')")
    public List<Incidence> getAllIncidences() {
        return incidenceUseCase.getAllIncidences();
    }

    @SchemaMapping(typeName = "Query", field = "getIncidenceById")
    @PreAuthorize("hasRole('MODERATOR')")
    public Optional<Incidence> getIncidenceById(@Argument Long id) {
        return incidenceUseCase.getIncidenceById(id);
    }

    @SchemaMapping(typeName = "Mutation", field = "createIncidence")
    public Incidence createIncidence(@Argument Long productId, @Argument ReportInput input) {
        String userEmail = getAuthenticatedUserEmail();
        Report report = Report.builder()
                .reason(input.reason())
                .comment(input.comment())
                .build();
        return incidenceUseCase.createIncidence(productId, report, userEmail);
    }

    @SchemaMapping(typeName = "Mutation", field = "resolveIncidence")
    @PreAuthorize("hasRole('MODERATOR')")
    public Incidence resolveIncidence(@Argument Long id, @Argument ResolveIncidenceInput input) {
        String moderatorEmail = getAuthenticatedUserEmail();
        return incidenceUseCase.resolveIncidence(id, input.decision(), input.moderatorComment(), moderatorEmail);
    }

    private String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !authentication.getPrincipal().equals("anonymousUser")) {
            return authentication.getName();
        }
        return null;
    }

    @SchemaMapping(typeName = "ReportInput")
    record ReportInput(
            @Schema(description = "Motivo de la incidencia", example = "Producto defectuoso", required = true) String reason,
            @Schema(description = "Comentario adicional", example = "El producto llegó roto") String comment
    ) {}

    @SchemaMapping(typeName = "ResolveIncidenceInput")
    record ResolveIncidenceInput(
            @Schema(description = "Decisión del moderador", example = "APPROVED", required = true) IncidenceDecision decision,
            @Schema(description = "Comentario del moderador", example = "Reembolso aprobado", required = true) String moderatorComment
    ) {}
}
