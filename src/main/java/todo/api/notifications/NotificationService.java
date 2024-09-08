package todo.api.notifications;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import todo.api.account.AccountErrorCode;
import todo.api.account.AccountRepository;
import todo.api.account.entity.Users;
import todo.api.common.exception.CustomException;
import todo.api.notifications.entity.Notifications;
import todo.api.notifications.entity.request.NotificationCreateReq;
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

    public Long notificationCreate(Long userId, NotificationCreateReq req) {
        Users user = accountRepository.findById(userId)
                .orElseThrow(() -> new CustomException(AccountErrorCode.NO_USERS));

        Todos todo = todoRepository.findById(req.todoId())
                .orElseThrow(() -> new CustomException(TodoErrorCode.NO_TODOS));

        Notifications notification = req.toEntity(user, todo);

        notificationRepository.save(notification);

        return notification.getId();
    }
}
