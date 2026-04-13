package com.ecom.domain.repository;

import com.ecom.domain.model.Product;

/**
 * T025 – Repository contract for {@link Product} entities.
 *
 * <p>Products only require the standard CRUD operations defined by
 * {@link BaseRepository}. Stock management and price validation are handled
 * at the service layer, not the repository.
 */
public interface ProductRepository extends BaseRepository<Product, String> {
}
