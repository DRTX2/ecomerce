package com.drtx.ecomerce.amazon.application.usecases.appeal;

import com.drtx.ecomerce.amazon.core.model.exceptions.DomainExceptionFactory;
import com.drtx.ecomerce.amazon.core.model.issues.Incidence;
import com.drtx.ecomerce.amazon.core.model.issues.IncidenceStatus;
import com.drtx.ecomerce.amazon.core.model.issues.Appeal;
import com.drtx.ecomerce.amazon.core.model.issues.AppealDecision;
import com.drtx.ecomerce.amazon.core.model.issues.AppealStatus;
import com.drtx.ecomerce.amazon.core.model.user.User;
import com.drtx.ecomerce.amazon.core.ports.in.rest.AppealUseCasePort;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.AppealRepositoryPort;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.IncidenceRepositoryPort;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppealUseCaseImpl implements AppealUseCasePort {

    private final AppealRepositoryPort appealRepository;
    private final IncidenceRepositoryPort incidenceRepository;
    private final UserRepositoryPort userRepository;

    @Override
    @Transactional
    public Appeal createAppeal(Long incidenceId, String reason, String sellerEmail) {
        Incidence incidence = incidenceRepository.findById(incidenceId)
                .orElseThrow(() -> DomainExceptionFactory.incidenceNotFound(incidenceId));

        return processCreateAppeal(incidence, reason, sellerEmail);
    }

    @Override
    @Transactional
    public Appeal createAppealByUuid(UUID incidenceUuid, String reason, String sellerEmail) {
        Incidence incidence = incidenceRepository.findByUuid(incidenceUuid)
                .orElseThrow(() -> DomainExceptionFactory.incidenceNotFound(incidenceUuid));

        return processCreateAppeal(incidence, reason, sellerEmail);
    }

    private Appeal processCreateAppeal(Incidence incidence, String reason, String sellerEmail) {
        // Validate incidence status
        if (incidence.getStatus() != IncidenceStatus.DECIDED && incidence.getStatus() != IncidenceStatus.CLOSED) {
            throw DomainExceptionFactory.invalidOperation(
                    "Incidence must be in DECIDED or CLOSED status to be appealed. Current status: "
                            + incidence.getStatus());
        }

        // Check if appeal already exists
        if (appealRepository.findByIncidenceId(incidence.getId()).isPresent()) {
            throw DomainExceptionFactory.invalidOperation("Appeal already exists for this incidence");
        }

        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> DomainExceptionFactory.userNotFound(sellerEmail));

        // Create appeal using setters
        Appeal appeal = new Appeal();
        appeal.setIncidence(incidence);
        appeal.setSeller(seller);
        appeal.setReason(reason);
        appeal.initializeDefaults();

        // Update Incidence status
        incidence.setStatus(IncidenceStatus.APPEALED);
        incidenceRepository.save(incidence);

        return appealRepository.save(appeal);
    }

    @Override
    public Optional<Appeal> getAppealById(Long id) {
        return appealRepository.findById(id);
    }

    @Override
    public Optional<Appeal> getAppealByUuid(UUID uuid) {
        return appealRepository.findByUuid(uuid);
    }

    @Override
    @Transactional
    public Appeal resolveAppeal(Long id, AppealDecision decision, String moderatorEmail) {
        Appeal appeal = appealRepository.findById(id)
                .orElseThrow(() -> DomainExceptionFactory.appealNotFound(id));

        User moderator = userRepository.findByEmail(moderatorEmail)
                .orElseThrow(() -> DomainExceptionFactory.userNotFound(moderatorEmail));

        appeal.setNewModerator(moderator);
        appeal.setFinalDecision(decision);
        appeal.setFinalDecisionAt(LocalDateTime.now());
        appeal.setStatus(AppealStatus.RESOLVED);

        return appealRepository.save(appeal);
    }

    @Override
    @Transactional
    public Appeal resolveAppealByUuid(UUID uuid, AppealDecision decision, String moderatorEmail) {
        Appeal appeal = appealRepository.findByUuid(uuid)
                .orElseThrow(() -> DomainExceptionFactory.appealNotFound(uuid));

        User moderator = userRepository.findByEmail(moderatorEmail)
                .orElseThrow(() -> DomainExceptionFactory.userNotFound(moderatorEmail));

        appeal.setNewModerator(moderator);
        appeal.setFinalDecision(decision);
        appeal.setFinalDecisionAt(LocalDateTime.now());
        appeal.setStatus(AppealStatus.RESOLVED);

        Incidence incidence = appeal.getIncidence();
        incidence.setStatus(decision == AppealDecision.GRANTED ? IncidenceStatus.DECIDED : IncidenceStatus.CLOSED); // to check after if this is correct
        incidenceRepository.save(incidence);

        return appealRepository.save(appeal);
    }
}
