package com.ecom.application.notification;

import com.ecom.domain.model.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * T012 – Stub implementation of {@link NotificationSender} for the ZALO channel.
 *
 * <p>In this phase the sender logs the dispatch rather than calling the Zalo
 * Official Account API. Replace with a real HTTP client call when moving to
 * production.
 */
@Slf4j
@Component
public class ZaloSender implements NotificationSender {

    @Override
    public void send(Notification notification) {
        log.info("[ZALO] Sending to recipientId={} bodyLength={}",
                notification.getRecipientId(),
                notification.getBody() != null ? notification.getBody().length() : 0);
    }
}
