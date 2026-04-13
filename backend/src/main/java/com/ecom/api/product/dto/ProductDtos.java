package com.ecom.api.product.dto;

import com.ecom.domain.model.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * T021 – DTO namespace for Product CRUD operations.
 */
public final class ProductDtos {

    private ProductDtos() {}

    // ── CREATE ────────────────────────────────────────────────────────────────

    @Schema(description = "Request body for creating a new product")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductCreateRequest {

        @Schema(description = "Product name", example = "Wireless Mouse", minLength = 2, maxLength = 255)
        @NotBlank(message = "name is required")
        @Size(min = 2, max = 255, message = "name must be between 2 and 255 characters")
        private String name;

        @Schema(description = "Unit price (>= 0)", example = "29.99")
        @NotNull(message = "price is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "price must be >= 0")
        private BigDecimal price;

        @Schema(description = "Available stock quantity (>= 0)", example = "150")
        @NotNull(message = "stock is required")
        @Min(value = 0, message = "stock must be >= 0")
        private Integer stock;
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    @Schema(description = "Request body for updating an existing product")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductUpdateRequest {

        @Schema(description = "Product name", example = "Wireless Mouse Pro", minLength = 2, maxLength = 255)
        @NotBlank(message = "name is required")
        @Size(min = 2, max = 255, message = "name must be between 2 and 255 characters")
        private String name;

        @Schema(description = "Unit price (>= 0)", example = "34.99")
        @NotNull(message = "price is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "price must be >= 0")
        private BigDecimal price;

        @Schema(description = "Available stock quantity (>= 0)", example = "200")
        @NotNull(message = "stock is required")
        @Min(value = 0, message = "stock must be >= 0")
        private Integer stock;
    }

    // ── RESPONSE ──────────────────────────────────────────────────────────────

    @Schema(description = "Product resource representation")
    @Value
    @Builder
    public static class ProductResponse {

        @Schema(description = "UUID identifier", example = "550e8400-e29b-41d4-a716-446655440000")
        String id;

        @Schema(description = "Product name", example = "Wireless Mouse")
        String name;

        @Schema(description = "Unit price", example = "29.99")
        BigDecimal price;

        @Schema(description = "Available stock quantity", example = "150")
        Integer stock;

        @Schema(description = "Creation timestamp (UTC / ISO-8601)", example = "2024-01-15T08:30:00Z")
        Instant createdAt;

        @Schema(description = "Last-updated timestamp (UTC / ISO-8601)", example = "2024-01-15T09:00:00Z")
        Instant updatedAt;

        public static ProductResponse from(Product product) {
            return ProductResponse.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .price(product.getPrice())
                    .stock(product.getStock())
                    .createdAt(product.getCreatedAt())
                    .updatedAt(product.getUpdatedAt())
                    .build();
        }
    }
}

/*
ProductDtos = container chứa DTO

ProductCreateRequest = nhận data create
ProductUpdateRequest = nhận data update
ProductResponse = trả data ra ngoài

@Value = immutable
@Builder = build object dễ dàng
validation = check input

from() = convert Entity → DTO
*/