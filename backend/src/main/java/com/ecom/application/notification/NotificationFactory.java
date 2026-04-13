package com.ecom.application.notification;

import com.ecom.domain.model.ChannelType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * T013 – Factory that resolves the correct {@link NotificationSender} for a
 * given {@link ChannelType}.
 *
 * <p>Adding a new channel requires:
 * <ol>
 *   <li>A new {@link ChannelType} enum value.</li>
 *   <li>A new {@link NotificationSender} implementation annotated with
 *       {@code @Component}.</li>
 *   <li>A new {@code case} entry in {@link #resolve(ChannelType)}.</li>
 * </ol>
 * No existing sender or service code needs to change (Open/Closed Principle).
 */
@Component
@RequiredArgsConstructor
public class NotificationFactory {

    private final EmailSender emailSender;
    private final SmsSender smsSender;
    private final ZaloSender zaloSender;

    /**
     * Return the sender that handles the specified channel.
     *
     * @param channel the target notification channel
     * @return the matching {@link NotificationSender}
     * @throws IllegalArgumentException if {@code channel} has no registered sender
     */
    public NotificationSender resolve(ChannelType channel) {
        return switch (channel) {
            case EMAIL -> emailSender;
            case SMS   -> smsSender;
            case ZALO  -> zaloSender;
        };
    }
}
