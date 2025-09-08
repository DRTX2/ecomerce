package com.drtx.ecomerce.amazon.application.usecases;

import com.drtx.ecomerce.amazon.core.model.User;
import com.drtx.ecomerce.amazon.core.model.UserRole;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.UserRepositoryPort;
import com.drtx.ecomerce.amazon.core.ports.out.security.PasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserUseCaseImplTest {

    private UserRepositoryPort repository;
    private PasswordService passwordService;
    private UserUseCaseImpl userUseCase;

    @BeforeEach
    void setUp() {
        repository = mock(UserRepositoryPort.class);
        passwordService = mock(PasswordService.class);
        userUseCase = new UserUseCaseImpl(repository, passwordService);
    }

    @Test
    void testGetUserById() {
        User user = new User(1L, "David", "d@test.com", "1234", "Ambato", "0999999999", UserRole.USER);
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userUseCase.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals("David", result.get().getName());
        verify(repository).findById(1L);
    }

    @Test
    void testGetAllUsers() {
        User u1 = new User(1L, "David", "d@test.com", "1234", "Ambato", "0999999999", UserRole.USER);
        User u2 = new User(2L, "Alice", "a@test.com", "abcd", "Quito", "0987654321", UserRole.ADMIN);

        when(repository.findAll()).thenReturn(List.of(u1, u2));

        List<User> users = userUseCase.getAllUsers();

        assertEquals(2, users.size());
        verify(repository).findAll();
    }

    @Test
    void testUpdateUser() {
        User updated = new User(null, "David Updated", null, null, "Quito", "0998887777", null);
        User saved = new User(1L, "David Updated", "d@test.com", "1234", "Quito", "0998887777", UserRole.USER);

        when(repository.updateById(1L, updated)).thenReturn(saved);

        User result = userUseCase.updateUser(1L, updated);

        assertEquals("David Updated", result.getName());
        assertEquals("Quito", result.getAddress());
        verify(repository).updateById(1L, updated);
    }

    @Test
    void testDeleteUser() {
        doNothing().when(repository).delete(1L);

        userUseCase.deleteUser(1L);

        verify(repository).delete(1L);
    }
}
