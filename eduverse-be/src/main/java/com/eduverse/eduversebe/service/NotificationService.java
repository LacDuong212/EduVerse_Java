package com.eduverse.eduversebe.service;

import com.eduverse.eduversebe.common.exception.AppException;
import com.eduverse.eduversebe.common.globalEnums.ErrorCodes;
import com.eduverse.eduversebe.common.globalEnums.NotificationType;
import com.eduverse.eduversebe.model.Notification;
import com.eduverse.eduversebe.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MongoTemplate mongoTemplate;

    public List<Notification> getUserNotifications(String userId) {
        return notificationRepository.findByUserId(
                userId,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
    }

    public void markAllAsRead(String userId) {
        Query query = new Query(Criteria.where("userId").is(userId).and("isRead").is(false));

        Update update = new Update().set("isRead", true);

        mongoTemplate.updateMulti(query, update, Notification.class);
    }

    public void markAsRead(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCodes.NOTIFICATION_NOT_FOUND));

        if (notification.getIsRead() == null || !notification.getIsRead()) {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        }
    }

    public void deleteAllNotifications(String userId) {
        notificationRepository.deleteAllByUserId(userId);
    }

    public void createNotification(String userId, String message, NotificationType type) {
        Notification noti = Notification.builder()
                .userId(userId)
                .message(message)
                .type(type)
                .isRead(false)
                .build();

        notificationRepository.save(noti);
    }
}
