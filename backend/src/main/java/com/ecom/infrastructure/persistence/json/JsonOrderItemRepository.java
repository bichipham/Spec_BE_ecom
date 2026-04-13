package com.ecom.infrastructure.persistence.json;

import com.ecom.domain.model.OrderItem;
import com.ecom.domain.repository.OrderItemRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * T031 – JSON-file-backed implementation of {@link OrderItemRepository}.
 *
 * <p>All records are stored in {@code <data-dir>/order_items.json} as a JSON array.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class JsonOrderItemRepository implements OrderItemRepository {

    static final String COLLECTION = "order_items";
    private static final TypeReference<List<OrderItem>> TYPE_REF = new TypeReference<>() {};

    private final JsonFileStore store;

    // ── BaseRepository ────────────────────────────────────────────────────────

    @Override
    public OrderItem save(OrderItem item) {
        List<OrderItem> all = store.readAll(COLLECTION, TYPE_REF);
        all.removeIf(i -> i.getId().equals(item.getId()));
        all.add(item);
        store.writeAll(COLLECTION, all);
        log.debug("Saved order-item id={}", item.getId());
        return item;
    }

    @Override
    public Optional<OrderItem> findById(String id) {
        return store.readAll(COLLECTION, TYPE_REF).stream()
                .filter(i -> i.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<OrderItem> findAll() {
        return store.readAll(COLLECTION, TYPE_REF);
    }

    @Override
    public void deleteById(String id) {
        List<OrderItem> all = store.readAll(COLLECTION, TYPE_REF);
        boolean removed = all.removeIf(i -> i.getId().equals(id));
        if (removed) {
            store.writeAll(COLLECTION, all);
            log.debug("Deleted order-item id={}", id);
        }
    }

    @Override
    public boolean existsById(String id) {
        return store.readAll(COLLECTION, TYPE_REF).stream()
                .anyMatch(i -> i.getId().equals(id));
    }

    // ── OrderItemRepository ───────────────────────────────────────────────────

    @Override
    public List<OrderItem> findByOrderId(String orderId) {
        return store.readAll(COLLECTION, TYPE_REF).stream()
                .filter(i -> i.getOrderId().equals(orderId))
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderItem> findByProductId(String productId) {
        return store.readAll(COLLECTION, TYPE_REF).stream()
                .filter(i -> i.getProductId().equals(productId))
                .collect(Collectors.toList());
    }
}
