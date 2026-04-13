package com.ecom.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

/**
 * T013 – Jackson configuration: UTC timezone + ISO-8601 datetime strings.
 *
 * <p>Customises the auto-configured {@link com.fasterxml.jackson.databind.ObjectMapper}
 * so that all {@link java.time.Instant} / {@link java.time.LocalDateTime} values are:
 * <ul>
 *   <li>serialised as ISO-8601 strings (not timestamps).</li>
 *   <li>interpreted in the UTC timezone.</li>
 * </ul>
 * These settings complement the properties in {@code application.yml}
 * ({@code spring.jackson.time-zone} and {@code serialization.write-dates-as-timestamps}).
 */
@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> builder
                .modules(new JavaTimeModule())
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .timeZone(TimeZone.getTimeZone("UTC"));
    }
}
