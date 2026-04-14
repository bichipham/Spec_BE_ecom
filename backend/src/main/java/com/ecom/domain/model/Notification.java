package com.ecom.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * T006 – Notification domain entity.
 *
 * <p>Stored in channel-specific JSON files:
 * <ul>
 *   <li>{@code data/notifications-email.json}</li>
 *   <li>{@code data/notifications-sms.json}</li>
 *   <li>{@code data/notifications-zalo.json}</li>
 * </ul>
 *
 * <p>State transitions:
 * <pre>
 *   (new) → PENDING → SENT   (sentAt is assigned)
 *   (new) → PENDING → FAILED (sentAt remains null)
 * </pre>
 *
 * <p>Validation rules enforced at the service layer:
 * <ul>
 *   <li>{@code body} is required and must not be blank.</li>
 *   <li>When {@code channel = SMS}: {@code body} must be ≤ 160 characters.</li>
 *   <li>{@code subject} is ignored (not stored) when channel is SMS or ZALO.</li>
 * </ul>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Notification {

    private String id;

    /** Recipient identifier — userId, phone number, or email address depending on channel. */
    private String recipientId;

    private ChannelType channel;

    /** Email subject line. Ignored for SMS and ZALO channels. */
    private String subject;

    private String body;

    @Builder.Default
    private NotificationStatus status = NotificationStatus.PENDING;

    private Instant createdAt;

    /** Set when status transitions to SENT; remains null for PENDING and FAILED. */
    private Instant sentAt;
}
