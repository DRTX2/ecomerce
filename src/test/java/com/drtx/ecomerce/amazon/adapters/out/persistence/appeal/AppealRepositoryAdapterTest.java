package com.drtx.ecomerce.amazon.adapters.out.persistence.appeal;

import com.drtx.ecomerce.amazon.adapters.out.persistence.category.CategoryEntity;
import com.drtx.ecomerce.amazon.adapters.out.persistence.category.CategoryPersistenceRepository;
import com.drtx.ecomerce.amazon.adapters.out.persistence.incidence.IncidenceEntity;
import com.drtx.ecomerce.amazon.adapters.out.persistence.incidence.IncidencePersistenceRepository;
import com.drtx.ecomerce.amazon.adapters.out.persistence.product.ProductEntity;
import com.drtx.ecomerce.amazon.adapters.out.persistence.product.ProductPersistenceRepository;
import com.drtx.ecomerce.amazon.adapters.out.persistence.user.UserEntity;
import com.drtx.ecomerce.amazon.adapters.out.persistence.user.UserPersistenceRepository;
import com.drtx.ecomerce.amazon.core.model.issues.Appeal;
import com.drtx.ecomerce.amazon.core.model.issues.AppealStatus;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DataJpaTest
@Import(AppealRepositoryAdapter.class)
@DisplayName("Appeal Repository Adapter Integration Tests")
class AppealRepositoryAdapterTest {

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @EntityScan(basePackages = "com.drtx.ecomerce.amazon.adapters.out.persistence")
    @EnableJpaRepositories(basePackages = "com.drtx.ecomerce.amazon.adapters.out.persistence")
    static class TestConfig {
    }

    @Autowired
    private AppealRepositoryAdapter adapter;

    @Autowired
    private AppealPersistenceRepository appealRepository;

    @Autowired
    private IncidencePersistenceRepository incidenceRepository;

    @Autowired
    private ProductPersistenceRepository productRepository;

    @Autowired
    private CategoryPersistenceRepository categoryRepository;

    @Autowired
    private UserPersistenceRepository userRepository;

    @MockitoBean
    private AppealPersistenceMapper mapper;

    @Test
    @DisplayName("Should save appeal")
    void testSave() {
        // Given
        CategoryEntity cat = categoryRepository.save(new CategoryEntity(null, "C", null, null));
        ProductEntity prod = productRepository
                .save(new ProductEntity(null, "P", "D", BigDecimal.ONE, cat, BigDecimal.ONE, null,
                        "SKU-" + System.nanoTime(), 100,
                        com.drtx.ecomerce.amazon.core.model.product.ProductStatus.ACTIVE, "slug-" + System.nanoTime(),
                        null, null));
        UserEntity seller = userRepository.save(new UserEntity(null, "S", "s@mail.com", "p", "a", "1", null));

        IncidenceEntity incidence = new IncidenceEntity();
        incidence.setProduct(prod);
        incidence.setStatus(IncidenceStatus.OPEN);
        incidence = incidenceRepository.save(incidence);

        Appeal appeal = new Appeal();

        AppealEntity entity = new AppealEntity();
        entity.setIncidence(incidence);
        entity.setSeller(seller);
        entity.setReason("It wasn't me");
        entity.setStatus(AppealStatus.PENDING);

        when(mapper.toEntity(appeal)).thenReturn(entity);
        when(mapper.toDomain(any(AppealEntity.class))).thenAnswer(inv -> {
            AppealEntity e = inv.getArgument(0);
            Appeal a = new Appeal();
            a.setId(e.getId());
            return a;
        });

        // When
        Appeal saved = adapter.save(appeal);

        // Then
        assertThat(saved.getId()).isNotNull();
        AppealEntity fromDb = appealRepository.findById(saved.getId()).orElseThrow();
        assertThat(fromDb.getIncidence().getId()).isEqualTo(incidence.getId());
        assertThat(fromDb.getSeller().getEmail()).isEqualTo("s@mail.com");
    }

    @Test
    @DisplayName("Should find appeal by incidence ID")
    void testFindByIncidenceId() {
        // Given
        CategoryEntity cat = categoryRepository.save(new CategoryEntity(null, "C2", null, null));
        ProductEntity prod = productRepository
                .save(new ProductEntity(null, "P2", "D", BigDecimal.ONE, cat, BigDecimal.ONE, null,
                        "SKU2-" + System.nanoTime(), 100, com.drtx.ecomerce.amazon.core.model.product.ProductStatus.ACTIVE, "slug2-" + System.nanoTime(), null, null));
        UserEntity seller = userRepository.save(new UserEntity(null, "S2", "s2@mail.com", "p", "a", "1", null));

        IncidenceEntity incidence = new IncidenceEntity();
        incidence.setProduct(prod);
        incidence.setStatus(IncidenceStatus.OPEN);
        incidence = incidenceRepository.save(incidence);

        AppealEntity entity = new AppealEntity();
        entity.setIncidence(incidence);
        entity.setSeller(seller);
        entity.setReason("Check pls");
        entity.setStatus(AppealStatus.PENDING);
        appealRepository.save(entity);

        Appeal domain = new Appeal();
        domain.setId(entity.getId());
        when(mapper.toDomain(any(AppealEntity.class))).thenReturn(domain);

        // When
        Optional<Appeal> found = adapter.findByIncidenceId(incidence.getId());

        // Then
        assertThat(found).isPresent();
    }
}
