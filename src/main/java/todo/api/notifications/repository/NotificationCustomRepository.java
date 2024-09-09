package todo.api.notifications.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import todo.api.notifications.entity.request.NotificationListReq;
import todo.api.notifications.entity.response.NotificationListRes;

public interface NotificationCustomRepository {

    Page<NotificationListRes> findNotificationList(Long userId,
            Pageable pageable, NotificationListReq req);

}
