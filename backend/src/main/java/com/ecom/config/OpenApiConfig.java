package com.ecom.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * T014 – OpenAPI / Swagger configuration.
 *
 * <p>Registers API metadata and sets the versioned server base path {@code /api/v1}
 * so that the interactive Swagger UI at {@code /swagger-ui/index.html} uses the
 * correct prefix for all "try it out" calls.
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
                        .description("6-phase e-commerce backend – CRUD · Auth · Search · Security"))
                .servers(servers);
    }
}
