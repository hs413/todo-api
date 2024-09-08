package todo.api.notifications.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import todo.api.notifications.entity.NotificationsHistory;

public interface NotificationHistoryRepository extends JpaRepository<NotificationsHistory, Long> {

    @Query("SELECT COUNT(nh) FROM NotificationsHistory nh WHERE nh.notifications.id = :notificationId and nh.version = :version")
    Long sentCount(@Param("notificationId") Long notificationId, @Param("version") Integer version);
}
