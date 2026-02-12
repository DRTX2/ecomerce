package com.drtx.ecomerce.amazon.core.model;

import com.drtx.ecomerce.amazon.core.model.user.User;
import com.drtx.ecomerce.amazon.core.model.user.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    @DisplayName("Should create user correctly")
    void testUserCreation() {
        UUID userUuid = UUID.randomUUID();
        User user = new User(1L, userUuid, "David", "email@test.com", "1234", "Ambato", "0999999999", UserRole.ADMIN, true, false);

        assertEquals(1L, user.getId());
        assertEquals(userUuid, user.getUuid());
        assertEquals("David", user.getName());
        assertEquals(UserRole.ADMIN, user.getRole());
    }

    @Test
    @DisplayName("Should update fields correctly")
    void testUpdateUser() {
        UUID userUuid = UUID.randomUUID();
        User user = new User(2L, userUuid, "Temp", "temp@test.com", "pass", "Quito", "111", UserRole.USER, true, false);
        user.setName("New Name");
        assertEquals("New Name", user.getName());
    }
}