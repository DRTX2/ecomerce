package com.drtx.ecomerce.amazon.adapters.out.persistence.product;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers(disabledWithoutDocker = true)
class PostgresStockReservationIntegrationTest {
    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("ecomerce_test")
            .withUsername("test")
            .withPassword("test");

    @BeforeAll
    static void migrate() {
        Flyway.configure()
                .dataSource(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())
                .locations("classpath:db/migration")
                .load()
                .migrate();
    }

    @Test
    void reservationIsAtomicAndNeverMakesStockNegative() throws Exception {
        try (Connection connection = DriverManager.getConnection(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())) {
            try (var category = connection.prepareStatement("insert into categories(name) values ('Testing') returning id")) {
                var result = category.executeQuery();
                result.next();
                long categoryId = result.getLong(1);
                try (var product = connection.prepareStatement("""
                        insert into products(name, description, price, category_id, sku, stock_quantity, status, slug, created_at)
                        values ('Keyboard', 'Mechanical', 10.00, ?, 'KEYBOARD-TC-1', 2, 'ACTIVE', 'keyboard-tc-1', current_timestamp)
                        returning id
                        """)) {
                    product.setLong(1, categoryId);
                    var productResult = product.executeQuery();
                    productResult.next();
                    long productId = productResult.getLong(1);

                    try (var reserve = connection.prepareStatement("""
                            update products set stock_quantity = stock_quantity - ?
                             where id = ? and stock_quantity >= ?
                            """)) {
                        reserve.setInt(1, 2);
                        reserve.setLong(2, productId);
                        reserve.setInt(3, 2);
                        assertThat(reserve.executeUpdate()).isEqualTo(1);

                        reserve.setInt(1, 1);
                        reserve.setLong(2, productId);
                        reserve.setInt(3, 1);
                        assertThat(reserve.executeUpdate()).isZero();
                    }
                }
            }
        }
    }
}
