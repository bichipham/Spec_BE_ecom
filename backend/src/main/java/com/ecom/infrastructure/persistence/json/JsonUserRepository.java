package com.ecom.infrastructure.persistence.json;

import com.ecom.domain.model.User;
import com.ecom.domain.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * T028 – JSON-file-backed implementation of {@link UserRepository}.
 *
 * <p>All records are stored in {@code <data-dir>/users.json} as a JSON array.
 * Reads and writes are delegated to {@link JsonFileStore}, which handles
 * per-collection locking and atomic file replacement.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class JsonUserRepository implements UserRepository {

    static final String COLLECTION = "users";
    private static final TypeReference<List<User>> TYPE_REF = new TypeReference<>() {};

    private final JsonFileStore store;

    // ── BaseRepository ────────────────────────────────────────────────────────

    @Override
    public User save(User user) {
        List<User> all = store.readAll(COLLECTION, TYPE_REF);
        all.removeIf(u -> u.getId().equals(user.getId()));
        all.add(user);
        store.writeAll(COLLECTION, all);
        log.debug("Saved user id={}", user.getId());
        return user;
    }

    @Override
    public Optional<User> findById(String id) {
        return store.readAll(COLLECTION, TYPE_REF).stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return store.readAll(COLLECTION, TYPE_REF);
    }

    @Override
    public void deleteById(String id) {
        List<User> all = store.readAll(COLLECTION, TYPE_REF);
        boolean removed = all.removeIf(u -> u.getId().equals(id));
        if (removed) {
            store.writeAll(COLLECTION, all);
            log.debug("Deleted user id={}", id);
        }
    }

    @Override
    public boolean existsById(String id) {
        return store.readAll(COLLECTION, TYPE_REF).stream()
                .anyMatch(u -> u.getId().equals(id));
    }

    // ── UserRepository ────────────────────────────────────────────────────────

    @Override
    public Optional<User> findByEmail(String email) {
        return store.readAll(COLLECTION, TYPE_REF).stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }
}
