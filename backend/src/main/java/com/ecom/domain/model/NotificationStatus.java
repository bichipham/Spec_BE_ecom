package com.ecom.domain.model;

/**
 * T005 – Lifecycle states for a {@link Notification}.
 *
 * <p>Allowed transitions:
 * <pre>
 *   (new) → PENDING → SENT
 *   (new) → PENDING → FAILED
 * </pre>
 * Reverse transitions are prohibited and enforced in {@code NotificationService}.
 */
public enum NotificationStatus {
    PENDING,
    SENT,
    FAILED
}
