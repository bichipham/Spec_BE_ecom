package com.ecom.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * T019 – OrderItem domain entity.
 *
 * <p>Stored in {@code data/order_items.json}. Represents one product line
 * within an order.
 *
 * <p>Validation rules (enforced in service layer):
 * <ul>
 *   <li>{@code quantity} &gt; 0 and must not exceed current product stock.</li>
 *   <li>{@code price} is a <em>snapshot</em> of the product price at creation
 *       time – it does not change when the product price changes later.</li>
 *   <li>Creating or updating an item triggers recalculation of
 *       {@link Order#getTotalAmount()} via {@code OrderService}.</li>
 * </ul>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderItem {

    private String id;
    private String orderId;
    private String productId;
    private Integer quantity;
    /** Price snapshot captured at the moment this item was created. */
    private BigDecimal price;
    private Instant createdAt;
    private Instant updatedAt;
}
