package com.ecom.domain.repository;

import com.ecom.domain.model.User;

import java.util.Optional;

/**
 * T024 – Repository contract for {@link User} entities.
 *
 * <p>Extends the generic {@link BaseRepository} and adds a domain-specific
 * lookup by e-mail address (required for uniqueness validation and future
 * authentication).
 */
public interface UserRepository extends BaseRepository<User, String> {

    /**
     * Find a user by their unique e-mail address.
     *
     * @param email the e-mail to search for (case-sensitive)
     * @return the matching user, or empty if none exists
     */
    Optional<User> findByEmail(String email);
}
