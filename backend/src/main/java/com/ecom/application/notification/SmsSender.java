package com.ecom.application.notification;

import com.ecom.domain.model.Notification;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * SMS channel strategy – sends via Twilio.
 *
 * <p>Configure via env vars {@code TWILIO_ACCOUNT_SID}, {@code TWILIO_AUTH_TOKEN},
 * {@code TWILIO_FROM_NUMBER} or override in {@code application.yml}.
 * {@code recipientId} must be an E.164 phone number, e.g. {@code +84901234567}.
 */
@Slf4j
@Component
public class SmsSender implements NotificationSender {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.from-number}")
    private String fromNumber;

    @Override
    public void send(Notification notification) {
        Twilio.init(accountSid, authToken);
        Message message = Message.creator(
                new PhoneNumber(notification.getRecipientId()),
                new PhoneNumber(fromNumber),
                notification.getBody()
        ).create();
        log.info("[SMS] Sent to={} sid={}", notification.getRecipientId(), message.getSid());
    }
}
