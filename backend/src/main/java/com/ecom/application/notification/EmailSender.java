package com.ecom.application.notification;

import com.ecom.domain.model.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * T010 – Stub implementation of {@link NotificationSender} for the EMAIL channel.
 *
 * <p>In this phase the sender logs the dispatch rather than calling a real SMTP
 * server. Replace the log statement with an actual JavaMail / SMTP integration
 * when moving to production.
 */
@Slf4j
@Component
public class EmailSender implements NotificationSender {

    @Override
    public void send(Notification notification) {
        log.info("[EMAIL] Sending to recipientId={} subject='{}' bodyLength={}",
                notification.getRecipientId(),
                notification.getSubject(),
                notification.getBody() != null ? notification.getBody().length() : 0);
    }
}
