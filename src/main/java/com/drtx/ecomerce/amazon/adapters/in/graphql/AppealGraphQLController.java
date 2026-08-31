package com.drtx.ecomerce.amazon.adapters.in.graphql;

import com.drtx.ecomerce.amazon.core.model.issues.Appeal;
import com.drtx.ecomerce.amazon.core.model.issues.AppealDecision;
import com.drtx.ecomerce.amazon.core.ports.in.rest.AppealUseCasePort;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class AppealGraphQLController {

    private final AppealUseCasePort appealUseCase;

    @SchemaMapping(typeName = "Mutation", field = "createAppeal")
    public Appeal createAppeal(
            @Argument @Schema(description = "ID de la incidencia a apelar", example = "1", required = true) Long incidenceId,
            @Argument @Schema(description = "Motivo de la apelación", example = "La incidencia fue resuelta injustamente", required = true) String reason) {
        String sellerEmail = getAuthenticatedUserEmail();
        return appealUseCase.createAppeal(incidenceId, reason, sellerEmail);
    }

    @SchemaMapping(typeName = "Mutation", field = "resolveAppeal")
    @PreAuthorize("hasRole('MODERATOR')")
    public Appeal resolveAppeal(
            @Argument @Schema(description = "ID de la apelación", example = "1", required = true) Long id,
            @Argument @Schema(description = "Decisión final", example = "UPHELD", required = true) AppealDecision decision) {
        String moderatorEmail = getAuthenticatedUserEmail();
        return appealUseCase.resolveAppeal(id, decision, moderatorEmail);
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
