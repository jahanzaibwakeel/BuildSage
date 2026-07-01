package com.buildsage.service;

import com.buildsage.api.PageResponse;
import com.buildsage.domain.Notification;
import com.buildsage.dto.NotificationDtos.NotificationResponse;
import com.buildsage.exception.NotFoundException;
import com.buildsage.repository.NotificationRepository;
import com.buildsage.security.CurrentUser;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> list(CurrentUser user, Pageable pageable) {
        return PageResponse.from(notificationRepository
                .findByUserIdOrderByCreatedAtDesc(user.id(), pageable)
                .map(this::toResponse));
    }

    @Transactional
    public NotificationResponse markRead(CurrentUser user, UUID id) {
        Notification notification = notificationRepository
                .findByIdAndUserId(id, user.id())
                .orElseThrow(() -> new NotFoundException("Notification not found"));
        notification.markRead();
        return toResponse(notification);
    }

    private NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getChannel(),
                notification.getMessage(),
                notification.isReadFlag(),
                notification.getCreatedAt());
    }
}
