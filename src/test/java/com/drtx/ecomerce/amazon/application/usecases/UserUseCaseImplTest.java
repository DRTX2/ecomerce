package com.drtx.ecomerce.amazon.application.usecases;

import com.drtx.ecomerce.amazon.core.model.User;
import com.drtx.ecomerce.amazon.core.model.UserRole;
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

import static org.assertj.core.api.Assertions.assertThat;
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
                "John Doe",
                "john.doe@example.com",
                "encodedPassword123",
                "123 Main St",
                "555-0100",
                UserRole.USER
        );
    }

    @Test
    @DisplayName("Should get user by ID successfully")
    void shouldGetUserByIdSuccessfully() {
        // Given
        Long userId = 1L;
        when(userRepositoryPort.findById(userId)).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userUseCase.getUserById(userId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(userId);
        assertThat(result.get().getName()).isEqualTo("John Doe");
        assertThat(result.get().getEmail()).isEqualTo("john.doe@example.com");
        verify(userRepositoryPort, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Should return empty when user not found by ID")
    void shouldReturnEmptyWhenUserNotFoundById() {
        // Given
        Long userId = 999L;
        when(userRepositoryPort.findById(userId)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userUseCase.getUserById(userId);

        // Then
        assertThat(result).isEmpty();
        verify(userRepositoryPort, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Should get all users successfully")
    void shouldGetAllUsersSuccessfully() {
        // Given
        User user2 = new User(
                2L,
                "Jane Smith",
                "jane.smith@example.com",
                "encodedPassword456",
                "456 Oak Ave",
                "555-0200",
                UserRole.USER
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
        // Given
        Long userId = 1L;
        User updatedUser = new User(
                userId,
                "John Doe Updated",
                "john.updated@example.com",
                "newEncodedPassword",
                "789 Pine Rd",
                "555-0300",
                UserRole.USER
        );

        when(userRepositoryPort.updateById(eq(userId), any(User.class))).thenReturn(updatedUser);

        // When
        User result = userUseCase.updateUser(userId, updatedUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getName()).isEqualTo("John Doe Updated");
        assertThat(result.getEmail()).isEqualTo("john.updated@example.com");
        verify(userRepositoryPort, times(1)).updateById(userId, updatedUser);
    }

    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUserSuccessfully() {
        // Given
        Long userId = 1L;
        doNothing().when(userRepositoryPort).delete(userId);

        // When
        userUseCase.deleteUser(userId);

        // Then
        verify(userRepositoryPort, times(1)).delete(userId);
    }

    @Test
    @DisplayName("Should handle delete for non-existent user")
    void shouldHandleDeleteForNonExistentUser() {
        // Given
        Long userId = 999L;
        doNothing().when(userRepositoryPort).delete(userId);

        // When
        userUseCase.deleteUser(userId);

        // Then
        verify(userRepositoryPort, times(1)).delete(userId);
    }
}
