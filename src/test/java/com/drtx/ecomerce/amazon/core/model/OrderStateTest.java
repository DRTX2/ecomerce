package com.drtx.ecomerce.amazon.core.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OrderStateTest {

    @Test
    public void testEnumValues() {
        assertEquals(OrderState.PENDING, OrderState.valueOf("PENDING"));
        assertEquals(4, OrderState.values().length);
    }
}