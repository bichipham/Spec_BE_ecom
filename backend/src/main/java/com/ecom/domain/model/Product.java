package com.ecom.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * T017 – Product domain entity.
 *
 * <p>Stored in {@code data/products.json}.
 *
 * <p>Validation rules (enforced in service layer):
 * <ul>
 *   <li>{@code price} ≥ 0</li>
 *   <li>{@code stock} ≥ 0; never goes negative during order processing.</li>
 * </ul>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {

    private String id;
    private String name;
    /** Decimal(12,2) – non-negative selling price. */
    private BigDecimal price;
    /** Available stock; decremented when order items are created/updated. */
    private Integer stock;
    private Instant createdAt;
    private Instant updatedAt;
}
