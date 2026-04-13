package com.ecom.infrastructure.persistence.json;

import com.ecom.domain.model.Product;
import com.ecom.domain.repository.ProductRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * T029 – JSON-file-backed implementation of {@link ProductRepository}.
 *
 * <p>All records are stored in {@code <data-dir>/products.json} as a JSON array.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class JsonProductRepository implements ProductRepository {

    static final String COLLECTION = "products";
    private static final TypeReference<List<Product>> TYPE_REF = new TypeReference<>() {};

    private final JsonFileStore store;

    // ── BaseRepository ────────────────────────────────────────────────────────

    @Override
    public Product save(Product product) {
        List<Product> all = store.readAll(COLLECTION, TYPE_REF);
        all.removeIf(p -> p.getId().equals(product.getId()));
        all.add(product);
        store.writeAll(COLLECTION, all);
        log.debug("Saved product id={}", product.getId());
        return product;
    }

    @Override
    public Optional<Product> findById(String id) {
        return store.readAll(COLLECTION, TYPE_REF).stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Product> findAll() {
        return store.readAll(COLLECTION, TYPE_REF);
    }

    @Override
    public void deleteById(String id) {
        List<Product> all = store.readAll(COLLECTION, TYPE_REF);
        boolean removed = all.removeIf(p -> p.getId().equals(id));
        if (removed) {
            store.writeAll(COLLECTION, all);
            log.debug("Deleted product id={}", id);
        }
    }

    @Override
    public boolean existsById(String id) {
        return store.readAll(COLLECTION, TYPE_REF).stream()
                .anyMatch(p -> p.getId().equals(id));
    }
}
