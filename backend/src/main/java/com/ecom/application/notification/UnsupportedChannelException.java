package com.ecom.application.notification;

import com.ecom.domain.model.ChannelType;

/**
 * T023 – Thrown by {@link NotificationFactory} when a {@link ChannelType} has
 * no registered sender, or when {@code channel} is {@code null}.
 *
 * <p>Handled by {@code GlobalExceptionHandler} → HTTP 400 Bad Request so the
 * caller receives a clear, typed error rather than a generic 500.
 */
public class UnsupportedChannelException extends RuntimeException {

    public UnsupportedChannelException(ChannelType channel) {
        super(channel == null
                ? "channel is required"
                : "Unsupported notification channel: " + channel.name());
    }
}
