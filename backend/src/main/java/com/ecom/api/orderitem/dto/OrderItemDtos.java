package com.ecom.api.orderitem.dto;

import com.ecom.domain.model.OrderItem;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "Request body for adding a line item to an order")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemCreateRequest {

        @Schema(description = "UUID of the parent order", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotBlank(message = "order_id is required")
        private String orderId;

        @Schema(description = "UUID of the product to add", example = "550e8400-e29b-41d4-a716-446655440001")
        @NotBlank(message = "product_id is required")
        private String productId;

        @Schema(description = "Number of units (>= 1)", example = "3")
        @NotNull(message = "quantity is required")
        @Min(value = 1, message = "quantity must be >= 1")
        private Integer quantity;
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    @Schema(description = "Request body for updating the quantity of a line item")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemUpdateRequest {

        @Schema(description = "Updated number of units (>= 1)", example = "5")
        @NotNull(message = "quantity is required")
        @Min(value = 1, message = "quantity must be >= 1")
        private Integer quantity;
    }

    // ── RESPONSE ──────────────────────────────────────────────────────────────

    @Schema(description = "Order line-item resource representation")
    @Value
    @Builder
    public static class OrderItemResponse {

        @Schema(description = "UUID identifier", example = "550e8400-e29b-41d4-a716-446655440000")
        String id;

        @Schema(description = "UUID of the parent order", example = "550e8400-e29b-41d4-a716-446655440001")
        String orderId;

        @Schema(description = "UUID of the product", example = "550e8400-e29b-41d4-a716-446655440002")
        String productId;

        @Schema(description = "Number of units", example = "3")
        Integer quantity;

        /** Price snapshot at creation time. */
        @Schema(description = "Price per unit at time of order creation (snapshot)", example = "29.99")
        BigDecimal price;

        @Schema(description = "Creation timestamp (UTC / ISO-8601)", example = "2024-01-15T08:30:00Z")
        Instant createdAt;

        @Schema(description = "Last-updated timestamp (UTC / ISO-8601)", example = "2024-01-15T09:00:00Z")
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
