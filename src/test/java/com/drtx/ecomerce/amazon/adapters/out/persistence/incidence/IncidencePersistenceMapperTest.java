package com.drtx.ecomerce.amazon.adapters.out.persistence.incidence;

import com.drtx.ecomerce.amazon.adapters.out.persistence.product.ProductPersistenceMapper;
import com.drtx.ecomerce.amazon.adapters.out.persistence.user.UserPersistenceMapper;
import com.drtx.ecomerce.amazon.core.model.issues.Incidence;
import com.drtx.ecomerce.amazon.core.model.issues.IncidenceDecision;
import com.drtx.ecomerce.amazon.core.model.issues.IncidenceStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {IncidencePersistenceMapperImpl.class})
class IncidencePersistenceMapperTest {

    @Autowired
    private IncidencePersistenceMapper mapper;

    @MockBean
    private ProductPersistenceMapper productMapper;

    @MockBean
    private UserPersistenceMapper userMapper;

    @Test
    void toDomain_ShouldMapFields() {
        // Arrange
        IncidenceEntity entity = new IncidenceEntity();
        entity.setId(1L);
        entity.setPublicUi(UUID.randomUUID());
        entity.setStatus(IncidenceStatus.OPEN);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setDecision(IncidenceDecision.PENDING);

        // Act
        Incidence domain = mapper.toDomain(entity);

        // Assert
        assertNotNull(domain);
        assertEquals(entity.getId(), domain.getId());
        assertEquals(entity.getPublicUi(), domain.getPublicUi());
        assertEquals(entity.getStatus(), domain.getStatus());
        assertEquals(entity.getDecision(), domain.getDecision());
    }

    @Test
    void toEntity_ShouldMapFields() {
        // Arrange
        Incidence domain = new Incidence();
        domain.setId(1L);
        domain.setPublicUi(UUID.randomUUID());
        domain.setStatus(IncidenceStatus.CLOSED);

        // Act
        IncidenceEntity entity = mapper.toEntity(domain);

        // Assert
        assertNotNull(entity);
        assertEquals(domain.getId(), entity.getId());
        assertEquals(domain.getPublicUi(), entity.getPublicUi());
        assertEquals(domain.getStatus(), entity.getStatus());
    }
}
