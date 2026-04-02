package com.ecom.domain.repository;

import java.util.List;
import java.util.Optional;

/**
 * T011 – Base repository contract for all domain entities.
 *
 * <p>All entity repositories extend this interface so that the infrastructure
 * layer (JSON adapters now, DB adapters later) can be swapped without touching
 * business logic.
 *
 * @param <T>  entity type
 * @param <ID> primary-key type (typically {@link String} UUID)
 */
public interface BaseRepository<T, ID> {

    /** Persist a new entity or replace an existing one by id. */
    T save(T entity);

    /** Retrieve an entity by its id, or empty if absent. */
    Optional<T> findById(ID id);

    /** Return all entities in the collection. */
    List<T> findAll();

    /** Remove the entity with the given id. No-op if absent. */
    void deleteById(ID id);

    /** Return {@code true} if an entity with the given id exists. */
    boolean existsById(ID id);
}
