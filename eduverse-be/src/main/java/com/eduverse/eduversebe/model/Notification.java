package com.eduverse.eduversebe.model;

import com.eduverse.eduversebe.common.globalEnums.NotificationType;
import com.eduverse.eduversebe.common.model.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notifications")
public class Notification extends BaseEntity {

    private String userId;

    @Builder.Default
    private NotificationType type = NotificationType.INFO;

    private String message;

    @Builder.Default
    private boolean isRead = false;
}
