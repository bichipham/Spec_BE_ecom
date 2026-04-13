package com.ecom.api.notification;

import com.ecom.api.notification.dto.NotificationDtos.NotificationResponse;
import com.ecom.api.notification.dto.NotificationDtos.SendNotificationRequest;
import com.ecom.application.notification.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * T016 – REST controller for {@code /api/v1/notifications}.
 *
 * <p>Exposes:
 * <ul>
 *   <li>{@code POST /send}  – dispatch a notification via the chosen channel.</li>
 *   <li>{@code GET  /{id}}  – retrieve a notification by id (T020).</li>
 * </ul>
 */
@Tag(name = "Notifications", description = "Multi-channel notification dispatch (Email, SMS, Zalo)")
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "Send a notification",
               description = "Dispatches a notification via Email, SMS, or Zalo. "
                             + "Returns the persisted record with final status (SENT or FAILED).")
    @ApiResponse(responseCode = "201", description = "Notification dispatched and persisted")
    @ApiResponse(responseCode = "400", description = "Validation error or unsupported channel")
    @PostMapping("/send")
    @ResponseStatus(HttpStatus.CREATED)
    public NotificationResponse send(@Valid @RequestBody SendNotificationRequest req) {
        return notificationService.send(req);
    }

    @Operation(summary = "Get notification by id",
               description = "Searches across all channel files (email → sms → zalo).")
    @ApiResponse(responseCode = "200", description = "Notification found")
    @ApiResponse(responseCode = "404", description = "Notification not found")
    @GetMapping("/{id}")
    public NotificationResponse findById(@PathVariable String id) {
        return notificationService.findById(id);
    }
}
