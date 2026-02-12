package com.drtx.ecomerce.amazon.adapters.out.persistence.incidence;

import com.drtx.ecomerce.amazon.adapters.out.persistence.category.CategoryEntity;
import com.drtx.ecomerce.amazon.adapters.out.persistence.category.CategoryPersistenceRepository;
import com.drtx.ecomerce.amazon.adapters.out.persistence.product.ProductEntity;
import com.drtx.ecomerce.amazon.adapters.out.persistence.product.ProductPersistenceRepository;
import com.drtx.ecomerce.amazon.adapters.out.persistence.user.UserEntity;
import com.drtx.ecomerce.amazon.adapters.out.persistence.user.UserPersistenceRepository;
import com.drtx.ecomerce.amazon.core.model.issues.Incidence;
import com.drtx.ecomerce.amazon.core.model.issues.IncidenceDecision;
import com.drtx.ecomerce.amazon.core.model.issues.IncidenceStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DataJpaTest
@Import(IncidenceRepositoryAdapter.class)
@DisplayName("Incidence Repository Adapter Integration Tests")
class IncidenceRepositoryAdapterTest {

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @EntityScan(basePackages = "com.drtx.ecomerce.amazon.adapters.out.persistence")
    @EnableJpaRepositories(basePackages = "com.drtx.ecomerce.amazon.adapters.out.persistence")
    static class TestConfig {
    }

    @Autowired
    private IncidenceRepositoryAdapter adapter;

    @Autowired
    private IncidencePersistenceRepository incidenceRepository;

    @Autowired
    private ProductPersistenceRepository productRepository;

    @Autowired
    private CategoryPersistenceRepository categoryRepository;

    @Autowired
    private UserPersistenceRepository userRepository;

    @MockitoBean
    private IncidencePersistenceMapper mapper;

    @Test
    @DisplayName("Should save incidence with reports")
    void testSave() {
        // Given
        CategoryEntity cat = new CategoryEntity();
        cat.setName("C");
        cat.setUuid(UUID.randomUUID());
        cat = categoryRepository.save(cat);

        ProductEntity prod = new ProductEntity();
        prod.setName("P");
        prod.setDescription("D");
        prod.setPrice(BigDecimal.ONE);
        prod.setCategory(cat);
        prod.setSku("SKU-INC");
        prod.setSlug("slug-inc");
        prod.setUuid(UUID.randomUUID());
        prod.setStockQuantity(10);
        prod.setStatus(com.drtx.ecomerce.amazon.core.model.product.ProductStatus.ACTIVE);
        prod = productRepository.save(prod);

        UserEntity user = new UserEntity();
        user.setName("U");
        user.setEmail("e@mail.com");
        user.setPassword("p");
        user.setUuid(UUID.randomUUID());
        user.setEnabled(true);
        user.setLocked(false);
        user = userRepository.save(user);

        Incidence incidence = new Incidence();

        IncidenceEntity entity = new IncidenceEntity();
        entity.setProduct(prod);
        entity.setStatus(IncidenceStatus.OPEN);
        entity.setUuid(UUID.randomUUID());

        ReportEntity report = new ReportEntity();
        report.setReason("Spam");
        report.setReporter(user);

        List<ReportEntity> reports = new ArrayList<>();
        reports.add(report);
        entity.setReports(reports);

        when(mapper.toEntity(incidence)).thenReturn(entity);
        when(mapper.toDomain(any(IncidenceEntity.class))).thenAnswer(inv -> {
            IncidenceEntity e = inv.getArgument(0);
            Incidence i = new Incidence();
            i.setId(e.getId());
            i.setUuid(e.getUuid());
            return i;
        });

        // When
        Incidence saved = adapter.save(incidence);

        // Then
        assertThat(saved.getId()).isNotNull();
        IncidenceEntity fromDb = incidenceRepository.findById(saved.getId()).orElseThrow();
        assertThat(fromDb.getReports()).hasSize(1);
    }

    @Test
    @DisplayName("Should find open incidence by product")
    void testFindByProductOpen() {
        // Given
        CategoryEntity cat = new CategoryEntity();
        cat.setName("C2");
        cat.setUuid(UUID.randomUUID());
        cat = categoryRepository.save(cat);

        ProductEntity prod = new ProductEntity();
        prod.setName("P2");
        prod.setDescription("D");
        prod.setPrice(BigDecimal.ONE);
        prod.setCategory(cat);
        prod.setSku("SKU-INC2");
        prod.setSlug("slug-inc2");
        prod.setUuid(UUID.randomUUID());
        prod.setStockQuantity(10);
        prod.setStatus(com.drtx.ecomerce.amazon.core.model.product.ProductStatus.ACTIVE);
        prod = productRepository.save(prod);

        IncidenceEntity entity = new IncidenceEntity();
        entity.setProduct(prod);
        entity.setStatus(IncidenceStatus.OPEN);
        entity.setUuid(UUID.randomUUID());
        incidenceRepository.save(entity);

        Incidence domain = new Incidence();
        domain.setId(entity.getId());
        when(mapper.toDomain(any(IncidenceEntity.class))).thenReturn(domain);

        // When
        Optional<Incidence> found = adapter.findByProductIdAndStatusOpen(prod.getId());

        // Then
        assertThat(found).isPresent();
    }

    @Test
    @DisplayName("Should update incidence")
    void testUpdate() {
        // Given
        CategoryEntity cat = new CategoryEntity();
        cat.setName("C3");
        cat.setUuid(UUID.randomUUID());
        cat = categoryRepository.save(cat);

        ProductEntity prod = new ProductEntity();
        prod.setName("P3");
        prod.setDescription("D");
        prod.setPrice(BigDecimal.ONE);
        prod.setCategory(cat);
        prod.setSku("SKU-INC3");
        prod.setSlug("slug-inc3");
        prod.setUuid(UUID.randomUUID());
        prod.setStockQuantity(10);
        prod.setStatus(com.drtx.ecomerce.amazon.core.model.product.ProductStatus.ACTIVE);
        prod = productRepository.save(prod);

        IncidenceEntity entity = new IncidenceEntity();
        entity.setProduct(prod);
        entity.setStatus(IncidenceStatus.OPEN);
        entity.setUuid(UUID.randomUUID());
        entity = incidenceRepository.save(entity);

        Incidence updateData = new Incidence();
        IncidenceEntity updateEntity = new IncidenceEntity();
        updateEntity.setId(entity.getId());
        updateEntity.setUuid(entity.getUuid());
        updateEntity.setStatus(IncidenceStatus.CLOSED);
        updateEntity.setProduct(prod);
        updateEntity.setDecision(IncidenceDecision.DELETE);

        when(mapper.toEntity(updateData)).thenReturn(updateEntity);
        when(mapper.toDomain(any(IncidenceEntity.class))).thenAnswer(inv -> {
            IncidenceEntity e = inv.getArgument(0);
            Incidence i = new Incidence();
            i.setId(e.getId());
            i.setStatus(e.getStatus());
            return i;
        });

        // When
        Incidence updated = adapter.updateById(entity.getId(), updateData);

        // Then
        assertThat(updated.getStatus()).isEqualTo(IncidenceStatus.CLOSED);

        IncidenceEntity fromDb = incidenceRepository.findById(entity.getId()).orElseThrow();
        assertThat(fromDb.getStatus()).isEqualTo(IncidenceStatus.CLOSED);
        assertThat(fromDb.getDecision()).isEqualTo(IncidenceDecision.DELETE);
    }
}
