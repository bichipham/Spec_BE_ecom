package com.ecom.api.orderitem.dto;

import com.ecom.domain.model.OrderItem;
import jakarta.validation.constraints.Min;
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
 * T023 – DTO namespace for OrderItem CRUD operations.
 *
 * <p>The {@code price} field in the response is a <em>snapshot</em> of the
 * product price captured at creation time. Clients do not supply price – it is
 * read from the product record by {@code OrderItemService}.
 *
 * <p>Creating or updating an item triggers {@code Order.totalAmount}
 * recalculation (handled in the service layer).
 */
public final class OrderItemDtos {

    private OrderItemDtos() {}

    // ── CREATE ────────────────────────────────────────────────────────────────

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemCreateRequest {

        @NotBlank(message = "order_id is required")
        private String orderId;

        @NotBlank(message = "product_id is required")
        private String productId;

        @NotNull(message = "quantity is required")
        @Min(value = 1, message = "quantity must be >= 1")
        private Integer quantity;
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemUpdateRequest {

        @NotNull(message = "quantity is required")
        @Min(value = 1, message = "quantity must be >= 1")
        private Integer quantity;
    }

    // ── RESPONSE ──────────────────────────────────────────────────────────────

    @Value
    @Builder
    public static class OrderItemResponse {
        String id;
        String orderId;
        String productId;
        Integer quantity;
        /** Price snapshot at creation time. */
        BigDecimal price;
        Instant createdAt;
        Instant updatedAt;

        public static OrderItemResponse from(OrderItem item) {
            return OrderItemResponse.builder()
                    .id(item.getId())
                    .orderId(item.getOrderId())
                    .productId(item.getProductId())
                    .quantity(item.getQuantity())
                    .price(item.getPrice())
                    .createdAt(item.getCreatedAt())
                    .updatedAt(item.getUpdatedAt())
                    .build();
        }
    }
}
