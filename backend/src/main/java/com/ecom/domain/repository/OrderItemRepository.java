package com.ecom.domain.repository;

import com.ecom.domain.model.OrderItem;

import java.util.List;

/**
 * T027 – Repository contract for {@link OrderItem} entities.
 *
 * <p>Adds domain-specific lookups needed for:
 * <ul>
 *   <li>Fetching all line items of an order (used when computing
 *       {@code Order.totalAmount} and when listing order details).</li>
 *   <li>Detecting which orders reference a product (used to block
 *       unsafe product deletion).</li>
 * </ul>
 */
public interface OrderItemRepository extends BaseRepository<OrderItem, String> {

    /**
     * Return all line items belonging to the given order.
     *
     * @param orderId UUID of the parent order
     * @return list of order items (empty if none found)
     */
    List<OrderItem> findByOrderId(String orderId);

    /**
     * Return all line items that reference the given product.
     *
     * <p>Used to guard against deleting a product that is still
     * referenced by existing order lines.
     *
     * @param productId UUID of the product
     * @return list of order items (empty if none found)
     */
    List<OrderItem> findByProductId(String productId);
}
