package com.drtx.ecomerce.amazon.adapters.in.graphql;

import com.drtx.ecomerce.amazon.core.model.issues.Appeal;
import com.drtx.ecomerce.amazon.core.model.issues.AppealDecision;
import com.drtx.ecomerce.amazon.core.ports.in.rest.AppealUseCasePort;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class AppealGraphQLController {

    private final AppealUseCasePort appealUseCase;

    @MutationMapping
    public Appeal createAppeal(@Argument Long incidenceId, @Argument String reason) {
        String sellerEmail = getAuthenticatedUserEmail();
        return appealUseCase.createAppeal(incidenceId, reason, sellerEmail);
    }

    @MutationMapping
    @PreAuthorize("hasRole('MODERATOR')")
    public Appeal resolveAppeal(@Argument Long id, @Argument AppealDecision decision) {
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
