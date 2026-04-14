package com.ecom.infrastructure.persistence.json;

import com.ecom.domain.model.ChannelType;
import com.ecom.domain.model.Notification;
import com.ecom.domain.repository.NotificationRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * T008 – JSON-file-backed implementation of {@link NotificationRepository}.
 *
 * <p>Each {@link ChannelType} maps to a dedicated JSON collection:
 * <ul>
 *   <li>{@code notifications-email} → {@code notifications-email.json}</li>
 *   <li>{@code notifications-sms}   → {@code notifications-sms.json}</li>
 *   <li>{@code notifications-zalo}  → {@code notifications-zalo.json}</li>
 * </ul>
 *
 * <p>Writes are routed to the collection that matches the notification's channel.
 * Reads by id scan collections in order: EMAIL → SMS → ZALO.
 *
 * <p>Migration readiness: replace this bean with a JPA {@code @Repository}
 * implementing {@link NotificationRepository} — no changes needed in
 * {@code NotificationService}.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class JsonNotificationRepository implements NotificationRepository {

    private static final TypeReference<List<Notification>> TYPE_REF = new TypeReference<>() {};

    /** Stable scan order for findById cross-channel search. */
    private static final List<String> SCAN_ORDER = List.of(
            collectionFor(ChannelType.EMAIL),
            collectionFor(ChannelType.SMS),
            collectionFor(ChannelType.ZALO)
    );

    private final JsonFileStore store;

    // ── BaseRepository ────────────────────────────────────────────────────────

    @Override
    public Notification save(Notification notification) {
        String collection = collectionFor(notification.getChannel());
        List<Notification> all = store.readAll(collection, TYPE_REF);
        all.removeIf(n -> n.getId().equals(notification.getId()));
        all.add(notification);
        store.writeAll(collection, all);
        log.debug("Saved notification id={} channel={}", notification.getId(), notification.getChannel());
        return notification;
    }

    /**
     * Search each channel file in EMAIL → SMS → ZALO order.
     *
     * @param id UUID of the notification
     * @return matching notification, or empty if not found in any channel
     */
    @Override
    public Optional<Notification> findById(String id) {
        for (String collection : SCAN_ORDER) {
            Optional<Notification> found = store.readAll(collection, TYPE_REF).stream()
                    .filter(n -> n.getId().equals(id))
                    .findFirst();
            if (found.isPresent()) {
                return found;
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Notification> findAll() {
        return SCAN_ORDER.stream()
                .flatMap(col -> store.readAll(col, TYPE_REF).stream())
                .toList();
    }

    @Override
    public void deleteById(String id) {
        for (String collection : SCAN_ORDER) {
            List<Notification> all = store.readAll(collection, TYPE_REF);
            boolean removed = all.removeIf(n -> n.getId().equals(id));
            if (removed) {
                store.writeAll(collection, all);
                log.debug("Deleted notification id={} from collection={}", id, collection);
                return;
            }
        }
    }

    @Override
    public boolean existsById(String id) {
        return findById(id).isPresent();
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    static String collectionFor(ChannelType channel) {
        return switch (channel) {
            case EMAIL -> "notifications-email";
            case SMS   -> "notifications-sms";
            case ZALO  -> "notifications-zalo";
        };
    }
}
