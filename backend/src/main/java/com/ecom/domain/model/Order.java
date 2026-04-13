package com.ecom.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * T018 – Order domain entity.
 *
 * <p>Stored in {@code data/orders.json}. {@code totalAmount} is kept in sync
 * by {@code OrderItemService} whenever items are added, updated, or removed.
 *
 * <p>Allowed state transitions:
 * <pre>
 *   PENDING → PAID → SHIPPED
 *   PENDING → CANCELLED
 * </pre>
 * Reverse transitions are prohibited (enforced in {@code OrderService}).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {

    private String id;
    private String userId;
    /** Computed from sum of all linked OrderItem (quantity × price). */
    private BigDecimal totalAmount;
    private OrderStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    public enum OrderStatus {
        PENDING,
        PAID,
        SHIPPED,
        CANCELLED
    }
}
