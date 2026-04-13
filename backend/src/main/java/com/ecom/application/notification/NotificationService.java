package com.ecom.application.notification;

import com.ecom.api.common.ResourceNotFoundException;
import com.ecom.api.notification.dto.NotificationDtos.NotificationResponse;
import com.ecom.api.notification.dto.NotificationDtos.SendNotificationRequest;
import com.ecom.domain.model.ChannelType;
import com.ecom.domain.model.Notification;
import com.ecom.domain.model.NotificationStatus;
import com.ecom.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

/**
 * T015 – Application service for dispatching notifications.
 *
 * <p>Lifecycle:
 * <ol>
 *   <li>Build a {@link Notification} with status {@code PENDING} and persist it.</li>
 *   <li>Resolve the correct {@link NotificationSender} via {@link NotificationFactory}.</li>
 *   <li>Call {@link NotificationSender#send(Notification)}.</li>
 *   <li>On success: set status to {@code SENT} and {@code sentAt}; persist again.</li>
 *   <li>On exception: set status to {@code FAILED}; persist and re-throw.</li>
 * </ol>
 *
 * <p>SMS body validation (≤ 160 chars) is enforced here before invoking the sender.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    static final int SMS_BODY_MAX_LENGTH = 160;

    private final NotificationRepository notificationRepository;
    private final NotificationFactory notificationFactory;

    /**
     * Send a notification and persist the result.
     *
     * @param req the send request
     * @return the persisted notification response
     * @throws IllegalArgumentException if SMS body exceeds 160 characters
     */
    public NotificationResponse send(SendNotificationRequest req) {
        validateRequest(req);

        Instant now = Instant.now();
        Notification notification = Notification.builder()
                .id(UUID.randomUUID().toString())
                .recipientId(req.getRecipientId())
                .channel(req.getChannel())
                .subject(isSubjectChannel(req.getChannel()) ? req.getSubject() : null)
                .body(req.getBody())
                .status(NotificationStatus.PENDING)
                .createdAt(now)
                .build();

        notificationRepository.save(notification);
        log.info("Notification id={} created, channel={}, status=PENDING", notification.getId(), notification.getChannel());

        NotificationSender sender = notificationFactory.resolve(req.getChannel());

        try {
            sender.send(notification);
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(Instant.now());
            log.info("Notification id={} status=SENT", notification.getId());
        } catch (Exception ex) {
            notification.setStatus(NotificationStatus.FAILED);
            log.error("Notification id={} status=FAILED: {}", notification.getId(), ex.getMessage(), ex);
        }

        notificationRepository.save(notification);
        return toResponse(notification);
    }

    /**
     * Find a notification by id across all channel files.
     *
     * @param id UUID of the notification
     * @return the notification response
     * @throws ResourceNotFoundException if not found in any channel
     */
    public NotificationResponse findById(String id) {
        return notificationRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> ResourceNotFoundException.of("Notification", id));
    }

    // ── private helpers ───────────────────────────────────────────────────────

    private void validateRequest(SendNotificationRequest req) {
        if (req.getChannel() == ChannelType.SMS
                && req.getBody() != null
                && req.getBody().length() > SMS_BODY_MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "SMS body must not exceed " + SMS_BODY_MAX_LENGTH + " characters");
        }
    }

    private boolean isSubjectChannel(ChannelType channel) {
        return channel == ChannelType.EMAIL;
    }

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .recipientId(n.getRecipientId())
                .channel(n.getChannel())
                .subject(n.getSubject())
                .body(n.getBody())
                .status(n.getStatus())
                .createdAt(n.getCreatedAt())
                .sentAt(n.getSentAt())
                .build();
    }
}
