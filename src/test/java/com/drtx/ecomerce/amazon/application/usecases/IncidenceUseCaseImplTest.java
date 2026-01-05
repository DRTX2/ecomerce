package com.drtx.ecomerce.amazon.application.usecases;
import com.drtx.ecomerce.amazon.application.usecases.incidence.IncidenceUseCaseImpl;

import com.drtx.ecomerce.amazon.core.model.issues.Incidence;
import com.drtx.ecomerce.amazon.core.model.issues.IncidenceStatus;
import com.drtx.ecomerce.amazon.core.model.product.Product;
import com.drtx.ecomerce.amazon.core.model.issues.Report;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.IncidenceRepositoryPort;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.ProductRepositoryPort;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncidenceUseCaseImplTest {

    @Mock
    private IncidenceRepositoryPort incidenceRepository;
    @Mock
    private ProductRepositoryPort productRepository;
    @Mock
    private UserRepositoryPort userRepository;

    @InjectMocks
    private IncidenceUseCaseImpl incidenceUseCase;

    private Report sampleReport;
    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleReport = Report.builder()
                .reason("Bad product")
                .comment("Broken")
                .build();
        
        sampleProduct = new Product();
        sampleProduct.setId(1L);
    }

    @Test
    void createIncidence_ShouldCreateNew_WhenNoneExists() {
        // Arrange
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(sampleProduct));
        when(incidenceRepository.findByProductIdAndStatusOpen(productId)).thenReturn(Optional.empty());
        when(incidenceRepository.save(any(Incidence.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Incidence result = incidenceUseCase.createIncidence(productId, sampleReport, null);

        // Assert
        assertNotNull(result);
        assertEquals(sampleProduct, result.getProduct());
        assertEquals(IncidenceStatus.OPEN, result.getStatus());
        assertEquals(1, result.getReports().size());
        verify(incidenceRepository).save(any(Incidence.class));
    }

    @Test
    void createIncidence_ShouldAttachToExisting_WhenOpenExists() {
        // Arrange
        Long productId = 1L;
        Incidence existingIncidence = new Incidence();
        existingIncidence.setId(100L);
        existingIncidence.setProduct(sampleProduct);
        existingIncidence.setStatus(IncidenceStatus.OPEN);
        existingIncidence.setReports(new ArrayList<>());
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(sampleProduct));
        when(incidenceRepository.findByProductIdAndStatusOpen(productId)).thenReturn(Optional.of(existingIncidence));
        when(incidenceRepository.save(any(Incidence.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Incidence result = incidenceUseCase.createIncidence(productId, sampleReport, null);

        // Assert
        assertEquals(100L, result.getId());
        assertEquals(1, result.getReports().size());
        verify(incidenceRepository).save(existingIncidence);
    }
}
