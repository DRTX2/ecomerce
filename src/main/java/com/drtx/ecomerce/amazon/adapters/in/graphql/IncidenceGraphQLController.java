package com.drtx.ecomerce.amazon.adapters.in.graphql;

import com.drtx.ecomerce.amazon.core.model.issues.Incidence;
import com.drtx.ecomerce.amazon.core.model.issues.IncidenceDecision;
import com.drtx.ecomerce.amazon.core.model.issues.Report;
import com.drtx.ecomerce.amazon.core.ports.in.rest.IncidenceUseCasePort;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class IncidenceGraphQLController {

    private final IncidenceUseCasePort incidenceUseCase;

    @QueryMapping
    public List<Incidence> getAllIncidences() {
        return incidenceUseCase.getAllIncidences();
    }

    @QueryMapping
    public Optional<Incidence> getIncidenceById(@Argument Long id) {
        return incidenceUseCase.getIncidenceById(id);
    }

    @MutationMapping
    public Incidence createIncidence(@Argument Long productId, @Argument ReportInput input) {
        String userEmail = getAuthenticatedUserEmail();
        Report report = Report.builder()
                .reason(input.reason())
                .comment(input.comment())
                .build();
        return incidenceUseCase.createIncidence(productId, report, userEmail);
    }

    @MutationMapping
    public Incidence resolveIncidence(@Argument Long id, @Argument ResolveIncidenceInput input) {
        String moderatorEmail = getAuthenticatedUserEmail();
        return incidenceUseCase.resolveIncidence(id, input.decision(), input.moderatorComment(), moderatorEmail);
    }

    private String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            return authentication.getName();
        }
        return null;
    }

    record ReportInput(String reason, String comment) {}
    record ResolveIncidenceInput(IncidenceDecision decision, String moderatorComment) {}
}
