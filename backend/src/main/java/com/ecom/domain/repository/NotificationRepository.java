package com.ecom.domain.repository;

import com.ecom.domain.model.Notification;

import java.util.Optional;

/**
 * T007 – Repository contract for {@link Notification} entities.
 *
 * <p>Extends {@link BaseRepository} with the full CRUD contract.
 * The infrastructure adapter ({@code JsonNotificationRepository}) routes
 * persistence to the correct channel-specific JSON file.
 *
 * <p>Replacing the JSON adapter with a JPA implementation requires only
 * swapping the Spring bean — no changes to the service layer.
 */
public interface NotificationRepository extends BaseRepository<Notification, String> {

    /**
     * Persist a new notification or replace an existing one by id.
     * The adapter routes writes to the channel-specific file.
     *
     * @param notification the notification to save
     * @return the saved notification (same instance)
     */
    @Override
    Notification save(Notification notification);

    /**
     * Find a notification by id, searching across all channel files.
     *
     * @param id the UUID to look up
     * @return the matching notification, or empty if not found in any channel
     */
    @Override
    Optional<Notification> findById(String id);
}
