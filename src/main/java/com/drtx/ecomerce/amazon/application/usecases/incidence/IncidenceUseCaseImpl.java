package com.drtx.ecomerce.amazon.application.usecases.incidence;

import com.drtx.ecomerce.amazon.core.model.exceptions.DomainExceptionFactory;
import com.drtx.ecomerce.amazon.core.model.issues.Incidence;
import com.drtx.ecomerce.amazon.core.model.issues.IncidenceDecision;
import com.drtx.ecomerce.amazon.core.model.issues.IncidenceStatus;
import com.drtx.ecomerce.amazon.core.model.issues.Report;
import com.drtx.ecomerce.amazon.core.model.product.Product;
import com.drtx.ecomerce.amazon.core.model.user.User;
import com.drtx.ecomerce.amazon.core.ports.in.rest.IncidenceUseCasePort;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.IncidenceRepositoryPort;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.ProductRepositoryPort;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IncidenceUseCaseImpl implements IncidenceUseCasePort {

    private final IncidenceRepositoryPort incidenceRepository;
    private final ProductRepositoryPort productRepository;
    private final UserRepositoryPort userRepository;

    @Override
    @Transactional
    public Incidence createIncidence(UUID productUuid, Report report, String reporterEmail) {
        Product product = productRepository.findByUuid(productUuid)
                .orElseThrow(() -> DomainExceptionFactory.productNotFound(productUuid));

        if (reporterEmail != null) {
            userRepository.findByEmail(reporterEmail).ifPresent(report::setReporter);
        }

        report.initializeDefaults();

        Optional<Incidence> existingIncidence = incidenceRepository.findByProductUuidAndStatusOpen(productUuid);

        if (existingIncidence.isPresent()) {
            Incidence incidence = existingIncidence.get();
            incidence.getReports().add(report);
            return incidenceRepository.save(incidence);
        } else {
            Incidence newIncidence = new Incidence();
            newIncidence.initializeDefaults();
            newIncidence.setProduct(product);
            newIncidence.getReports().add(report);
            return incidenceRepository.save(newIncidence);
        }
    }

    @Override
    @Deprecated
    public Incidence createIncidence(Long productId, Report report, String reporterEmail) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> DomainExceptionFactory.productNotFound(productId));
        return createIncidence(product.getUuid(), report, reporterEmail);
    }

    @Override
    public Optional<Incidence> getIncidenceById(Long id) {
        return incidenceRepository.findById(id);
    }

    @Override
    public Optional<Incidence> getIncidenceByUuid(UUID uuid) {
        return incidenceRepository.findByUuid(uuid);
    }

    @Override
    public List<Incidence> getAllIncidences() {
        return incidenceRepository.findAll();
    }


    @Override
    @Transactional
    public Incidence resolveIncidenceByUuid(UUID uuid, IncidenceDecision decision, String moderatorComment, String moderatorEmail) {
        Incidence incidence = incidenceRepository.findByUuid(uuid)
                .orElseThrow(() -> DomainExceptionFactory.incidenceNotFound(uuid));

        User moderator = userRepository.findByEmail(moderatorEmail)
                .orElseThrow(() -> DomainExceptionFactory.userNotFound(moderatorEmail));

        incidence.setModerator(moderator);
        incidence.setDecision(decision);
        incidence.setModeratorComment(moderatorComment);
        incidence.setStatus(IncidenceStatus.DECIDED);

        return incidenceRepository.save(incidence);
    }

    @Override
    @Deprecated
    public Incidence resolveIncidence(Long id, IncidenceDecision decision, String moderatorComment,
            String moderatorEmail) {
        Incidence incidence = incidenceRepository.findById(id)
                .orElseThrow(() -> DomainExceptionFactory.incidenceNotFound(id));
        return resolveIncidenceByUuid(incidence.getUuid(), decision, moderatorComment, moderatorEmail);
    }

    @Override
    @Transactional
    public Incidence updateIncidenceByUuid(UUID uuid, Incidence incidence) {
        Incidence existing = incidenceRepository.findByUuid(uuid)
                .orElseThrow(() -> DomainExceptionFactory.incidenceNotFound(uuid));

        // Update fields if needed
        return incidenceRepository.save(existing);
    }

    @Override
    public void deleteIncidenceByUuid(UUID uuid) {
        incidenceRepository.deleteByUuid(uuid);
    }
}
