package todo.api.notifications.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import todo.api.notifications.entity.Notifications;

public interface NotificationRepository extends JpaRepository<Notifications, Long> {

    boolean existsByUserIdAndTodoId(Long userId, Long todoId);
}
