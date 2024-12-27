package com.example.awsspring.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.awsspring.common.DBTestContainer;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=validate")
@ImportTestcontainers(DBTestContainer.class)
class SchemaValidationTest {

    @Autowired private DataSource dataSource;

    @Test
    void testSchemaValidity() {
        assertThat(dataSource).isInstanceOf(HikariDataSource.class);
    }
}
