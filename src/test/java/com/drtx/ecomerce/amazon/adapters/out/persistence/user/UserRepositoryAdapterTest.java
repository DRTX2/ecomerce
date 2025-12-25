package com.drtx.ecomerce.amazon.adapters.out.persistence.user;

import com.drtx.ecomerce.amazon.core.model.user.User;
import com.drtx.ecomerce.amazon.core.model.user.UserRole;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DataJpaTest
@Import(UserRepositoryPortAdapter.class)
@DisplayName("User Repository Adapter Integration Tests")
class UserRepositoryAdapterTest {

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @EntityScan(basePackageClasses = UserEntity.class)
    @EnableJpaRepositories(basePackageClasses = UserPersistenceRepository.class)
    static class TestConfig {
    }

    @Autowired
    private UserRepositoryPortAdapter adapter;

    @Autowired
    private UserPersistenceRepository repository;

    @MockitoBean
    private UserPersistenceMapper mapper;

    @Test
    @DisplayName("Should save a new user")
    void testSave() {
        // Given
        User user = new User();
        user.setEmail("john@example.com");

        UserEntity entity = new UserEntity();
        entity.setEmail("john@example.com");
        // entity.setId(1L); // REMOVED: Let DB generate ID for proper INSERT

        when(mapper.toEntity(user)).thenReturn(entity);
        when(mapper.toDomain(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity e = invocation.getArgument(0);
            User u = new User();
            u.setId(e.getId());
            u.setEmail(e.getEmail());
            return u;
        });

        // When
        User savedUser = adapter.save(user);

        // Then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("john@example.com");
        assertThat(repository.findByEmail("john@example.com")).isPresent();
    }

    @Test
    @DisplayName("Should find user by ID")
    void testFindById() {
        // Given
        UserEntity entity = new UserEntity();
        entity.setEmail("jane@example.com");
        entity = repository.save(entity);

        User domainUser = new User();
        domainUser.setId(entity.getId());
        domainUser.setEmail("jane@example.com");

        when(mapper.toDomain(any(UserEntity.class))).thenReturn(domainUser);

        // When
        Optional<User> foundUser = adapter.findById(entity.getId());

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("jane@example.com");
    }

    @Test
    @DisplayName("Should find user by Email")
    void testFindByEmail() {
        // Given
        UserEntity entity = new UserEntity();
        entity.setEmail("bob@example.com");
        repository.save(entity);

        User domainUser = new User();
        domainUser.setEmail("bob@example.com");

        when(mapper.toDomain(any(UserEntity.class))).thenReturn(domainUser);

        // When
        Optional<User> foundUser = adapter.findByEmail("bob@example.com");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("bob@example.com");
    }

    @Test
    @DisplayName("Should throw exception when saving existing email")
    void testSaveDuplicateEmail() {
        // Given
        UserEntity entity = new UserEntity();
        entity.setEmail("duplicate@example.com");
        repository.save(entity);

        User newUser = new User();
        newUser.setEmail("duplicate@example.com");

        when(mapper.toEntity(newUser)).thenReturn(new UserEntity());

        // When & Then
        assertThatThrownBy(() -> adapter.save(newUser))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email already exists");
    }

    @Test
    @DisplayName("Should update existing user")
    void testUpdateById() {
        // Given
        UserEntity entity = new UserEntity();
        entity.setEmail("old@example.com");
        entity.setName("Old Name");
        entity = repository.save(entity);

        User updateData = new User();
        updateData.setName("New Name");
        updateData.setEmail("new@example.com");
        updateData.setAddress("Addr");
        updateData.setPhone("123");
        updateData.setRole(UserRole.USER);

        User updatedDomain = new User();
        updatedDomain.setName("New Name");
        updatedDomain.setEmail("new@example.com");

        when(mapper.toDomain(any(UserEntity.class))).thenReturn(updatedDomain);

        // When
        User updatedUser = adapter.updateById(entity.getId(), updateData);

        // Then
        assertThat(updatedUser.getName()).isEqualTo("New Name");

        UserEntity inDb = repository.findById(entity.getId()).orElseThrow();
        assertThat(inDb.getName()).isEqualTo("New Name");
        assertThat(inDb.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    @DisplayName("Should delete user")
    void testDelete() {
        // Given
        UserEntity entity = new UserEntity();
        entity.setEmail("delete@example.com");
        entity = repository.save(entity);

        // When
        adapter.delete(entity.getId());

        // Then
        assertThat(repository.existsById(entity.getId())).isFalse();
    }
}
