package com.ecom.infrastructure.persistence.json;

import com.ecom.domain.model.Order;
import com.ecom.domain.repository.OrderRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * T030 – JSON-file-backed implementation of {@link OrderRepository}.
 *
 * <p>All records are stored in {@code <data-dir>/orders.json} as a JSON array.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class JsonOrderRepository implements OrderRepository {

    static final String COLLECTION = "orders";
    private static final TypeReference<List<Order>> TYPE_REF = new TypeReference<>() {};

    private final JsonFileStore store;

    // ── BaseRepository ────────────────────────────────────────────────────────

    @Override
    public Order save(Order order) {
        List<Order> all = store.readAll(COLLECTION, TYPE_REF);
        all.removeIf(o -> o.getId().equals(order.getId()));
        all.add(order);
        store.writeAll(COLLECTION, all);
        log.debug("Saved order id={}", order.getId());
        return order;
    }

    @Override
    public Optional<Order> findById(String id) {
        return store.readAll(COLLECTION, TYPE_REF).stream()
                .filter(o -> o.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Order> findAll() {
        return store.readAll(COLLECTION, TYPE_REF);
    }

    @Override
    public void deleteById(String id) {
        List<Order> all = store.readAll(COLLECTION, TYPE_REF);
        boolean removed = all.removeIf(o -> o.getId().equals(id));
        if (removed) {
            store.writeAll(COLLECTION, all);
            log.debug("Deleted order id={}", id);
        }
    }

    @Override
    public boolean existsById(String id) {
        return store.readAll(COLLECTION, TYPE_REF).stream()
                .anyMatch(o -> o.getId().equals(id));
    }

    // ── OrderRepository ───────────────────────────────────────────────────────

    @Override
    public List<Order> findByUserId(String userId) {
        return store.readAll(COLLECTION, TYPE_REF).stream()
                .filter(o -> o.getUserId().equals(userId))
                .collect(Collectors.toList());
    }
}
