package com.WW;

import static org.assertj.core.api.Assertions.assertThat;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.MigrateResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest(properties = {
		"spring.mail.host=localhost",
		"spring.mail.port=2525",
		"spring.mail.username=test-user",
		"spring.mail.password=test-password",
		"app.mail.from=test@example.invalid",
		"app.mail.first-appointment-recipients=clinic@example.invalid",
		"app.security.jwt.secret=dGVzdC1vbmx5LWtleS10aGF0LWlzLWF0LWxlYXN0LTMyLWJ5dGVzLWxvbmc="
})
@Testcontainers(disabledWithoutDocker = true)
class LbpAppVetApplicationTests {
	@Container
	static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17.10-alpine3.22")
			.withDatabaseName("lbp_app_vet_test")
			.withUsername("lbp_test")
			.withPassword("lbp_test");

	@DynamicPropertySource
	static void databaseProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
	}

	@Autowired
	private Flyway flyway;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	void flywayBuildsSchemaHibernateValidatesAndRestartIsIdempotent() {
		assertThat(flyway.info().current().getVersion().getVersion()).isEqualTo("2");
		assertThat(jdbcTemplate.queryForObject("select count(*) from ruoli", Integer.class)).isEqualTo(4);

		MigrateResult secondMigration = flyway.migrate();
		assertThat(secondMigration.migrationsExecuted).isZero();
	}

}
