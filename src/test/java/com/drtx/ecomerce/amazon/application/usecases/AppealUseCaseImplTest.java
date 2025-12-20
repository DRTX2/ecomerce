package com.drtx.ecomerce.amazon.application.usecases;

import com.drtx.ecomerce.amazon.core.model.*;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.AppealRepositoryPort;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.IncidenceRepositoryPort;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppealUseCaseImplTest {

    @Mock
    private AppealRepositoryPort appealRepository;
    @Mock
    private IncidenceRepositoryPort incidenceRepository;
    @Mock
    private UserRepositoryPort userRepository;

    @InjectMocks
    private AppealUseCaseImpl appealUseCase;

    @Test
    void createAppeal_ShouldSucceed_WhenIncidenceDecided() {
        // Arrange
        Long incidenceId = 10L;
        String sellerEmail = "seller@test.com";
        
        Incidence incidence = new Incidence();
        incidence.setId(incidenceId);
        incidence.setStatus(IncidenceStatus.DECIDED);
        
        User seller = new User(null, "Name", sellerEmail, "pwd", "addr", "123", UserRole.USER);

        when(incidenceRepository.findById(incidenceId)).thenReturn(Optional.of(incidence));
        when(appealRepository.findByIncidenceId(incidenceId)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(sellerEmail)).thenReturn(Optional.of(seller));
        when(appealRepository.save(any(Appeal.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Appeal result = appealUseCase.createAppeal(incidenceId, "Unfair!", sellerEmail);

        // Assert
        assertNotNull(result);
        assertEquals(AppealStatus.PENDING, result.getStatus());
        assertEquals(IncidenceStatus.APPEALED, incidence.getStatus());
        verify(incidenceRepository).save(incidence);
    }

    @Test
    void createAppeal_ShouldThrow_WhenIncidenceOpen() {
        // Arrange
        Long incidenceId = 10L;
        Incidence incidence = new Incidence();
        incidence.setStatus(IncidenceStatus.OPEN); // Not appealable
        
        when(incidenceRepository.findById(incidenceId)).thenReturn(Optional.of(incidence));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            appealUseCase.createAppeal(incidenceId, "Try", "email")
        );
    }
}
