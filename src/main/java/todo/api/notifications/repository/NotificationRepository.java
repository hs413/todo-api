package todo.api.notifications.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import todo.api.notifications.entity.Notifications;

public interface NotificationRepository extends JpaRepository<Notifications, Long>,
        NotificationCustomRepository {

    boolean existsByUserIdAndTodoId(Long userId, Long todoId);

    Optional<Notifications> findByIdAndUserId(Long id, Long userId);
}
