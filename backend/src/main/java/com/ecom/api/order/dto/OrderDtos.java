package com.ecom.api.order.dto;

import com.ecom.domain.model.Order;
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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderCreateRequest {

        @NotBlank(message = "user_id is required")
        private String userId;
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderUpdateRequest {

        @NotNull(message = "status is required")
        private Order.OrderStatus status;
    }

    // ── RESPONSE ──────────────────────────────────────────────────────────────

    @Value
    @Builder
    public static class OrderResponse {
        String id;
        String userId;
        BigDecimal totalAmount;
        Order.OrderStatus status;
        Instant createdAt;
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
