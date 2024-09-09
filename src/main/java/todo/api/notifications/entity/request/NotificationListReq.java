package todo.api.notifications.entity.request;

import java.time.LocalDateTime;

public record NotificationListReq(
        String keyword,
        LocalDateTime from,
        LocalDateTime to
) {

}
