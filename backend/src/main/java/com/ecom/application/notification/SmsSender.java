package com.ecom.application.notification;

import com.ecom.domain.model.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * T011 – Stub implementation of {@link NotificationSender} for the SMS channel.
 *
 * <p>SMS body is limited to 160 characters; validation is enforced at the service
 * layer before this sender is invoked. Replace the log statement with a real SMS
 * gateway (e.g., Twilio, VNPT SMS) when moving to production.
 */
@Slf4j
@Component
public class SmsSender implements NotificationSender {

    @Override
    public void send(Notification notification) {
        log.info("[SMS] Sending to recipientId={} bodyLength={}",
                notification.getRecipientId(),
                notification.getBody() != null ? notification.getBody().length() : 0);
    }
}
