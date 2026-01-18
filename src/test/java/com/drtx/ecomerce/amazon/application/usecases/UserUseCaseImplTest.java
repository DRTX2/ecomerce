package com.drtx.ecomerce.amazon.application.usecases;
import com.drtx.ecomerce.amazon.application.usecases.user.UserUseCaseImpl;

import com.drtx.ecomerce.amazon.core.model.user.User;
import com.drtx.ecomerce.amazon.core.model.user.UserRole;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.UserRepositoryPort;
import com.drtx.ecomerce.amazon.core.ports.out.security.PasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserUseCaseImpl Unit Tests")
class UserUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private PasswordService passwordService;

    @InjectMocks
    private UserUseCaseImpl userUseCase;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User(
                1L,
                UUID.randomUUID(),
                "John Doe",
                "john.doe@example.com",
                "encodedPassword123",
                "123 Main St",
                "555-0100",
                UserRole.USER,
                true,
                false
        );
    }

    @Test
    @DisplayName("Should get user by UUID successfully")
    void shouldGetUserByUuidSuccessfully() {
        // Given
        UUID uuid = testUser.getUuid();
        when(userRepositoryPort.findByUuid(uuid))
                .thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userUseCase.getUserByUuid(uuid);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getUuid()).isEqualTo(uuid);
        assertThat(result.get().getName()).isEqualTo("John Doe");

        verify(userRepositoryPort).findByUuid(uuid);
    }

    @Test
    @DisplayName("Should return empty when user not found by ID")
    void shouldReturnEmptyWhenUserNotFoundById() {
        // Given
        UUID userUuid = UUID.randomUUID();
        when(userRepositoryPort.findByUuid(userUuid)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userUseCase.getUserByUuid(userUuid);

        // Then
        assertThat(result).isEmpty();
        verify(userRepositoryPort, times(1)).findByUuid(userUuid);
    }

    @Test
    @DisplayName("Should get all users successfully")
    void shouldGetAllUsersSuccessfully() {
        // Given
        UUID userUUID = UUID.randomUUID();
        User user2 = new User(
                2L,
                userUUID,
                "Jane Smith",
                "jane.smith@example.com",
                "encodedPassword456",
                "456 Oak Ave",
                "555-0200",
                UserRole.USER,
                true,
                false
        );

        List<User> users = Arrays.asList(testUser, user2);
        when(userRepositoryPort.findAll()).thenReturn(users);

        // When
        List<User> result = userUseCase.getAllUsers();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(testUser, user2);
        verify(userRepositoryPort, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no users exist")
    void shouldReturnEmptyListWhenNoUsersExist() {
        // Given
        when(userRepositoryPort.findAll()).thenReturn(List.of());

        // When
        List<User> result = userUseCase.getAllUsers();

        // Then
        assertThat(result).isEmpty();
        verify(userRepositoryPort, times(1)).findAll();
    }

    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUserSuccessfully() {
        UUID uuid = testUser.getUuid();

        when(userRepositoryPort.findByUuid(uuid))
                .thenReturn(Optional.of(testUser));

        when(userRepositoryPort.updateByUuid(eq(uuid), any(User.class)))
                .thenReturn(testUser);

        User result = userUseCase.updateUserByUuid(uuid, testUser);

        assertThat(result).isNotNull();
        assertThat(result.getUuid()).isEqualTo(uuid);

        verify(userRepositoryPort).findByUuid(uuid);
        verify(userRepositoryPort).updateByUuid(eq(uuid), any(User.class));
    }

    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUserSuccessfully() {
        UUID uuid = testUser.getUuid();

        when(userRepositoryPort.findByUuid(uuid))
                .thenReturn(Optional.of(testUser));

        userUseCase.deleteUserByUuid(uuid);

        verify(userRepositoryPort).findByUuid(uuid);
        verify(userRepositoryPort).deleteByUuid(uuid);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent user")
    void shouldThrowWhenDeletingNonExistentUser() {
        UUID uuid = UUID.randomUUID();

        when(userRepositoryPort.findByUuid(uuid))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userUseCase.deleteUserByUuid(uuid))
                .isInstanceOf(RuntimeException.class); // o tu excepción de dominio
    }
}
