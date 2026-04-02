package com.ecom.api.product.dto;

import com.ecom.domain.model.Product;
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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductCreateRequest {

        @NotBlank(message = "name is required")
        @Size(min = 2, max = 255, message = "name must be between 2 and 255 characters")
        private String name;

        @NotNull(message = "price is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "price must be >= 0")
        private BigDecimal price;

        @NotNull(message = "stock is required")
        @Min(value = 0, message = "stock must be >= 0")
        private Integer stock;
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductUpdateRequest {

        @NotBlank(message = "name is required")
        @Size(min = 2, max = 255, message = "name must be between 2 and 255 characters")
        private String name;

        @NotNull(message = "price is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "price must be >= 0")
        private BigDecimal price;

        @NotNull(message = "stock is required")
        @Min(value = 0, message = "stock must be >= 0")
        private Integer stock;
    }

    // ── RESPONSE ──────────────────────────────────────────────────────────────

    @Value
    @Builder
    public static class ProductResponse {
        String id;
        String name;
        BigDecimal price;
        Integer stock;
        Instant createdAt;
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