package com.healthlink.clinicsystem.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "HealthLink Clinic API", version = "1.0"),   // API metadata
        security = @SecurityRequirement(name = "bearerAuth")              // Apply JWT security globally
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,   // HTTP-based auth
        scheme = "bearer",                // Bearer token
        bearerFormat = "JWT"              // Token format
)
public class SwaggerConfig {
    // No additional config required
}
