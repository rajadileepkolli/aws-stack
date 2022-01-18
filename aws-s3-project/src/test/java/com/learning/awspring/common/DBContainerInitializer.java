package com.learning.awspring.common;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

public class DBContainerInitializer {

  @Container
  private static final PostgreSQLContainer<?> sqlContainer =
      new PostgreSQLContainer<>("postgres:12.3")
          .withDatabaseName("integration-tests-db")
          .withUsername("username")
          .withPassword("password");

  static {
    sqlContainer.start();
  }

  @DynamicPropertySource
  public static void setPostGreSQLValues(DynamicPropertyRegistry dynamicPropertyRegistry) {
    dynamicPropertyRegistry.add("spring.datasource.url", sqlContainer::getJdbcUrl);
    dynamicPropertyRegistry.add("spring.datasource.username", sqlContainer::getUsername);
    dynamicPropertyRegistry.add("spring.datasource.password", sqlContainer::getPassword);
  }
}
