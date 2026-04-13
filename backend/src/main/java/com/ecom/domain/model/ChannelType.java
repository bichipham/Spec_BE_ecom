package com.ecom.domain.model;

/**
 * T004 – Supported notification channel types.
 *
 * <p>Add a new enum value and a matching {@code NotificationSender} implementation
 * to support additional channels without modifying existing code (Open/Closed Principle).
 */
public enum ChannelType {
    EMAIL,
    SMS,
    ZALO
}
