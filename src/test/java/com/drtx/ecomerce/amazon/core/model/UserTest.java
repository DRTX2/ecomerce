package com.drtx.ecomerce.amazon.core.model;

import com.drtx.ecomerce.amazon.core.model.user.User;
import com.drtx.ecomerce.amazon.core.model.user.UserRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    public void testUserCreation() {
        User user = new User(1L, "David", "email@test.com", "1234", "Ambato", "0999999999", UserRole.ADMIN);

        assertEquals(1L, user.getId());
        assertEquals("David", user.getName());
        assertEquals("email@test.com", user.getEmail());
        assertEquals(UserRole.ADMIN, user.getRole());
    }

    @Test
    public void testSetters() {
        User user = new User(2L, "Temp", "temp@test.com", "pass", "Quito", "111", UserRole.USER);

        user.setName("Updated");
        user.setRole(UserRole.MODERATOR);

        assertEquals("Updated", user.getName());
        assertEquals(UserRole.MODERATOR, user.getRole());
    }
}