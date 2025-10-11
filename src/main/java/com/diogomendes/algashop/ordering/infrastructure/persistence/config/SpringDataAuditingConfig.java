package com.diogomendes.algashop.ordering.infrastructure.persistence.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.UUID;

import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.Optional.of;

@Configuration
@EnableJpaAuditing(
        dateTimeProviderRef = "auditingDateTimeProvider",
        auditorAwareRef = "auditorProvider"
)
public class SpringDataAuditingConfig {

    @Bean
    public DateTimeProvider auditingDateTimeProvider() {
        return () -> of(now().truncatedTo(MILLIS));
    }

    @Bean
    public AuditorAware<UUID> auditorProvider() {
        return () -> of(UUID.randomUUID());
    }

}
