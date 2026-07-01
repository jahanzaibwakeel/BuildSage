package com.buildsage.controller;

import com.buildsage.api.ApiResponse;
import com.buildsage.api.PageResponse;
import com.buildsage.dto.NotificationDtos.NotificationResponse;
import com.buildsage.security.CurrentUser;
import com.buildsage.service.NotificationService;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    ApiResponse<PageResponse<NotificationResponse>> list(@AuthenticationPrincipal CurrentUser user, Pageable pageable) {
        return ApiResponse.ok(notificationService.list(user, pageable));
    }

    @PostMapping("/{id}/read")
    ApiResponse<NotificationResponse> markRead(@AuthenticationPrincipal CurrentUser user, @PathVariable UUID id) {
        return ApiResponse.ok(notificationService.markRead(user, id));
    }
}
