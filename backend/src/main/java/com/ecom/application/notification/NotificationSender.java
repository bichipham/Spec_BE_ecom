package com.ecom.application.notification;

import com.ecom.domain.model.Notification;

/**
 * T009 – Contract for channel-specific notification senders.
 *
 * <p>Each supported channel provides exactly one implementation:
 * {@link EmailSender}, {@link SmsSender}, {@link ZaloSender}.
 *
 * <p>Adding a new channel requires only implementing this interface and
 * registering the new {@link com.ecom.domain.model.ChannelType} value —
 * no changes to existing senders or {@link NotificationFactory} (Open/Closed).
 */
public interface NotificationSender {

    /**
     * Send the given notification via the channel this sender represents.
     *
     * @param notification the notification to dispatch; status is PENDING on entry
     * @throws RuntimeException if the send operation fails; caller is responsible
     *                          for transitioning status to FAILED
     */
    void send(Notification notification);
}
