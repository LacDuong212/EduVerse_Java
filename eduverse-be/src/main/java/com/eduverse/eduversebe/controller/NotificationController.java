package com.eduverse.eduversebe.controller;

import com.eduverse.eduversebe.common.api.ApiResponse;
import com.eduverse.eduversebe.common.globalEnums.SuccessCodes;
import com.eduverse.eduversebe.model.Notification;
import com.eduverse.eduversebe.model.User;
import com.eduverse.eduversebe.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Notification>>> getUserNotifications(
            @AuthenticationPrincipal User currentUser
    ) {
        List<Notification> list = notificationService.getUserNotifications(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(SuccessCodes.GET_USER_NOTIFICATIONS_SUCCESS, list));
    }

    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<String>> markAllAsRead(
            @AuthenticationPrincipal User currentUser
    ) {
        notificationService.markAllAsRead(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(SuccessCodes.ALL_NOTIFICATIONS_MARKED));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<String>> markAsRead(@PathVariable String id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success(SuccessCodes.NOTIFICATION_MARKED));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> clearAllNotifications(
            @AuthenticationPrincipal User currentUser
    ) {
        notificationService.deleteAllNotifications(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(SuccessCodes.ALL_NOTIFICATIONS_DELETED));
    }
}
