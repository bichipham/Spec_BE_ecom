package com.ecom.api.order.dto;

import com.ecom.domain.model.Order;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * T022 – DTO namespace for Order CRUD operations.
 *
 * <p>Orders are created in {@link Order.OrderStatus#PENDING} state.
 * The {@code totalAmount} is computed automatically from order items –
 * it is not accepted as input.
 *
 * <p>State transitions (enforced in {@code OrderService}):
 * <pre>PENDING → PAID → SHIPPED</pre>
 * <pre>PENDING → CANCELLED</pre>
 */
public final class OrderDtos {

    private OrderDtos() {}

    // ── CREATE ────────────────────────────────────────────────────────────────

    @Schema(description = "Request body for creating a new order")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderCreateRequest {

        @Schema(description = "UUID of the user placing the order", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotBlank(message = "user_id is required")
        private String userId;
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    @Schema(description = "Request body for updating order status")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderUpdateRequest {

        @Schema(
                description = "New order status – valid transitions: PENDING→PAID→SHIPPED or PENDING→CANCELLED",
                example = "PAID",
                allowableValues = {"PENDING", "PAID", "SHIPPED", "CANCELLED"})
        @NotNull(message = "status is required")
        private Order.OrderStatus status;
    }

    // ── RESPONSE ──────────────────────────────────────────────────────────────

    @Schema(description = "Order resource representation")
    @Value
    @Builder
    public static class OrderResponse {

        @Schema(description = "UUID identifier", example = "550e8400-e29b-41d4-a716-446655440000")
        String id;

        @Schema(description = "UUID of the owning user", example = "550e8400-e29b-41d4-a716-446655440001")
        String userId;

        @Schema(description = "Total order amount – sum of all line items", example = "99.97")
        BigDecimal totalAmount;

        @Schema(description = "Current order status", example = "PENDING")
        Order.OrderStatus status;

        @Schema(description = "Creation timestamp (UTC / ISO-8601)", example = "2024-01-15T08:30:00Z")
        Instant createdAt;

        @Schema(description = "Last-updated timestamp (UTC / ISO-8601)", example = "2024-01-15T09:00:00Z")
        Instant updatedAt;

        public static OrderResponse from(Order order) {
            return OrderResponse.builder()
                    .id(order.getId())
                    .userId(order.getUserId())
                    .totalAmount(order.getTotalAmount())
                    .status(order.getStatus())
                    .createdAt(order.getCreatedAt())
                    .updatedAt(order.getUpdatedAt())
                    .build();
        }
    }
}
