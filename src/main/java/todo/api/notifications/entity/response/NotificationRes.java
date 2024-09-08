package todo.api.notifications.entity.response;

import java.time.LocalDateTime;
import todo.api.notifications.entity.Notifications;
import todo.api.notifications.entity.RepeatUnit;

public record NotificationRes(
        LocalDateTime dueDate,
        String message,
        Integer repeatCount,
        Integer repeatInterval,
        RepeatUnit repeatUnit
) {

    public NotificationRes(Notifications notification) {
        this(
                notification.getDueDate(),
                notification.getMessage(),
                notification.getRepeatCount(),
                notification.getRepeatInterval(),
                notification.getRepeatUnit()
        );
    }
}
