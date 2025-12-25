package com.drtx.ecomerce.amazon.core.model;

import com.drtx.ecomerce.amazon.core.model.user.UserRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserRoleTest {

    @Test
    public void testAuthorities() {
        assertEquals("ROLE_USER", UserRole.USER.getAuthority());
        assertEquals("ROLE_ADMIN", UserRole.ADMIN.getAuthority());
    }

    @Test
    public void testRoleName() {
        assertEquals("MODERATOR", UserRole.MODERATOR.roleName());
    }
}