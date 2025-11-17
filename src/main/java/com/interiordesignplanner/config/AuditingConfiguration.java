package com.interiordesignplanner.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing // Enables auditing
@Configuration
public class AuditingConfiguration {

}
