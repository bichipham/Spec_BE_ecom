package com.ecom.domain.repository;

import com.ecom.domain.model.Order;

import java.util.List;

/**
 * T026 – Repository contract for {@link Order} entities.
 *
 * <p>Adds a domain-specific lookup by user so that all orders belonging to
 * a given user can be retrieved without loading the full collection.
 */
public interface OrderRepository extends BaseRepository<Order, String> {

    /**
     * Return all orders placed by the given user.
     *
     * @param userId UUID of the owning user
     * @return list of orders (empty if none found)
     */
    List<Order> findByUserId(String userId);
}
