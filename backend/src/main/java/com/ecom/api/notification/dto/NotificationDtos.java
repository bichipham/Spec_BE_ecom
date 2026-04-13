package com.ecom.api.notification.dto;

import com.ecom.domain.model.ChannelType;
import com.ecom.domain.model.NotificationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.Instant;

/**
 * T014 – DTO namespace for Notification API operations.
 */
public final class NotificationDtos {

    private NotificationDtos() {}

    // ── REQUEST ───────────────────────────────────────────────────────────────

    @Schema(description = "Request body for sending a notification")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SendNotificationRequest {

        @Schema(description = "Recipient identifier — userId, phone number, or email address depending on channel",
                example = "user-001")
        @NotBlank(message = "recipient_id is required")
        private String recipientId;

        @Schema(description = "Delivery channel", example = "EMAIL",
                allowableValues = {"EMAIL", "SMS", "ZALO"})
        @NotNull(message = "channel is required")
        private ChannelType channel;

        @Schema(description = "Subject line — used for EMAIL only, ignored for SMS and ZALO",
                example = "Welcome to our platform")
        private String subject;

        @Schema(description = "Message body — max 160 chars for SMS",
                example = "Your OTP is 123456")
        @NotBlank(message = "body is required")
        private String body;
    }

    // ── RESPONSE ──────────────────────────────────────────────────────────────

    @Schema(description = "Notification resource representation")
    @Value
    @Builder
    public static class NotificationResponse {

        @Schema(description = "UUID identifier", example = "550e8400-e29b-41d4-a716-446655440000")
        String id;

        @Schema(description = "Recipient identifier", example = "user-001")
        String recipientId;

        @Schema(description = "Delivery channel", example = "EMAIL")
        ChannelType channel;

        @Schema(description = "Subject — present only for EMAIL", example = "Welcome to our platform")
        String subject;

        @Schema(description = "Message body", example = "Your OTP is 123456")
        String body;

        @Schema(description = "Current status of the notification", example = "SENT")
        NotificationStatus status;

        @Schema(description = "UTC timestamp when the notification was created")
        Instant createdAt;

        @Schema(description = "UTC timestamp when the notification was sent; null if PENDING or FAILED")
        Instant sentAt;
    }
}
