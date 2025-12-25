package com.drtx.ecomerce.amazon.application.usecases;

import com.drtx.ecomerce.amazon.core.model.issues.Incidence;
import com.drtx.ecomerce.amazon.core.model.issues.IncidenceDecision;
import com.drtx.ecomerce.amazon.core.model.issues.IncidenceStatus;
import com.drtx.ecomerce.amazon.core.model.issues.Report;
import com.drtx.ecomerce.amazon.core.model.product.Product;
import com.drtx.ecomerce.amazon.core.ports.in.rest.IncidenceUseCasePort;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.IncidenceRepositoryPort;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.ProductRepositoryPort;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IncidenceUseCaseImpl implements IncidenceUseCasePort {

    private final IncidenceRepositoryPort incidenceRepository;
    private final ProductRepositoryPort productRepository;
    private final UserRepositoryPort userRepository;

    @Override
    @Transactional
    public Incidence createIncidence(Long productId, Report report, String reporterEmail) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id " + productId));

        if (reporterEmail != null) {
            userRepository.findByEmail(reporterEmail).ifPresent(report::setReporter);
        }

        report.initializeDefaults();

        Optional<Incidence> existingIncidence = incidenceRepository.findByProductIdAndStatusOpen(productId);

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
    public Optional<Incidence> getIncidenceById(Long id) {
        return incidenceRepository.findById(id);
    }

    @Override
    public List<Incidence> getAllIncidences() {
        return incidenceRepository.findAll();
    }

    @Override
    @Transactional
    public Incidence resolveIncidence(Long id, IncidenceDecision decision, String moderatorComment, String moderatorEmail) {
        Incidence incidence = incidenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incidence not found with id " + id));

        if (moderatorEmail != null) {
            userRepository.findByEmail(moderatorEmail).ifPresent(incidence::setModerator);
        }
        
        incidence.setDecision(decision);
        incidence.setModeratorComment(moderatorComment);
        incidence.setStatus(IncidenceStatus.DECIDED);
        
        return incidenceRepository.save(incidence);
    }

    @Override
    @Transactional
    public Incidence updateIncidence(Long id, Incidence incidence) {
        return incidenceRepository.updateById(id, incidence);
    }
}
