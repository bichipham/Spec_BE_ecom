package com.ecom.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * T014 – OpenAPI / Swagger configuration.
 *
 * <p>Registers API metadata, tag descriptions, server base URL, and a pre-wired
 * JWT Bearer security scheme (will be activated in Phase 2 – T047).
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        List<Server> servers = new ArrayList<>();
        servers.add(new Server().url("/").description("Application root"));

        return new OpenAPI()
                .info(new Info()
                        .title("Ecom Backend API")
                        .version("1.0.0")
                        .description("6-phase e-commerce backend – CRUD · Auth · Search · Security · Scaling · Production")
                        .contact(new Contact()
                                .name("Ecom Team")
                                .url("https://github.com/ecomspeckit/Spec_BE_ecom"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(servers)
                // ── Tag descriptions (displayed in Swagger UI sidebar) ────────────
                .addTagsItem(new Tag().name("Health").description("Liveness and readiness probes"))
                .addTagsItem(new Tag().name("Users").description("User management – CRUD operations"))
                .addTagsItem(new Tag().name("Products").description("Product catalogue – CRUD operations"))
                .addTagsItem(new Tag().name("Orders").description("Order lifecycle – create, update status, cancel"))
                .addTagsItem(new Tag().name("Order Items").description("Line items within an order"))
                // ── Security scheme (Phase 2) ────────────────────────────────────
                // Registered here so controllers can reference it via @SecurityRequirement.
                // No global SecurityRequirement is added yet – Phase 1 is fully open.
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT obtained from POST /api/v1/auth/login (Phase 2)")));
    }
}
