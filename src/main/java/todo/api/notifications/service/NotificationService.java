package todo.api.notifications.service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import todo.api.account.AccountErrorCode;
import todo.api.account.AccountRepository;
import todo.api.account.entity.Users;
import todo.api.common.exception.CustomException;
import todo.api.notifications.NotificationErrorCode;
import todo.api.notifications.entity.Notifications;
import todo.api.notifications.entity.request.NotificationCreateReq;
import todo.api.notifications.repository.NotificationRepository;
import todo.api.todo.TodoErrorCode;
import todo.api.todo.entity.Todos;
import todo.api.todo.repository.TodoRepository;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final AccountRepository accountRepository;
    private final TodoRepository todoRepository;
    private final NotificationRedisService notificationRedisService;

    public Long notificationCreate(Long userId, NotificationCreateReq req) {
        Users user = accountRepository.findById(userId)
                .orElseThrow(() -> new CustomException(AccountErrorCode.NO_USERS));

        Todos todo = todoRepository.findById(req.todoId())
                .orElseThrow(() -> new CustomException(TodoErrorCode.NO_TODOS));

        if (req.dueDate().isBefore(LocalDateTime.now())) {
            throw new CustomException(NotificationErrorCode.NOT_BEFORE_NOW);
        }

        boolean exists = notificationRepository
                .existsByUserIdAndTodoId(userId, req.todoId());

        // 이미 등록된 경우 예외처리
        if (exists) {
            throw new CustomException(NotificationErrorCode.ALREADY_CREATED);
        }

        Notifications notification = req.toEntity(user, todo);

        notificationRepository.save(notification);

        notificationRedisService.scheduleNotification(notification);

        return notification.getId();
    }
}
