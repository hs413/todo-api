package todo.api.notifications.entity.response;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;

public record NotificationListRes(
        Long id,
        LocalDateTime dueDate,
        String todoTitle
) {

    @QueryProjection
    public NotificationListRes {

    }
}
