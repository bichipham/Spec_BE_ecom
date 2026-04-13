package com.ecom.application.notification;

import com.ecom.domain.model.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * EMAIL channel strategy – sends via Spring Mail (Gmail SMTP).
 *
 * <p>Configure {@code spring.mail.username} and {@code spring.mail.password}
 * (Gmail App Password) in {@code application.yml} or via env vars
 * {@code MAIL_USERNAME} / {@code MAIL_PASSWORD}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailSender implements NotificationSender {

    private final JavaMailSender mailSender;

    @Override
    public void send(Notification notification) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(notification.getRecipientId());
        msg.setSubject(notification.getSubject() != null ? notification.getSubject() : "(no subject)");
        msg.setText(notification.getBody());

        mailSender.send(msg);
        log.info("[EMAIL] Sent to={} subject='{}'",
                notification.getRecipientId(), notification.getSubject());
    }
}
