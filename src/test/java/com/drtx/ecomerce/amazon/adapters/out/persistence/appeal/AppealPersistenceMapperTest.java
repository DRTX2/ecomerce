package com.drtx.ecomerce.amazon.adapters.out.persistence.appeal;

import com.drtx.ecomerce.amazon.adapters.out.persistence.incidence.IncidencePersistenceMapper;
import com.drtx.ecomerce.amazon.adapters.out.persistence.user.UserPersistenceMapper;
import com.drtx.ecomerce.amazon.core.model.issues.Appeal;
import com.drtx.ecomerce.amazon.core.model.issues.AppealStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {AppealPersistenceMapperImpl.class})
class AppealPersistenceMapperTest {

    @Autowired
    private AppealPersistenceMapper mapper;

    @MockBean
    private IncidencePersistenceMapper incidenceMapper;

    @MockBean
    private UserPersistenceMapper userMapper;

    @Test
    void toDomain_ShouldMapFields() {
        // Arrange
        AppealEntity entity = new AppealEntity();
        entity.setId(5L);
        entity.setReason("Test Reason");
        entity.setStatus(AppealStatus.PENDING);

        // Act
        Appeal domain = mapper.toDomain(entity);

        // Assert
        assertNotNull(domain);
        assertEquals(entity.getId(), domain.getId());
        assertEquals(entity.getReason(), domain.getReason());
        assertEquals(entity.getStatus(), domain.getStatus());
    }
    
    @Test
    void toEntity_ShouldMapFields() {
        // Arrange
        Appeal domain = new Appeal();
        domain.setId(5L);
        domain.setReason("Test Reason");
        domain.setStatus(AppealStatus.RESOLVED);

        // Act
        AppealEntity entity = mapper.toEntity(domain);

        // Assert
        assertNotNull(entity);
        assertEquals(domain.getId(), entity.getId());
        assertEquals(domain.getReason(), entity.getReason());
        assertEquals(domain.getStatus(), entity.getStatus());
    }
}
