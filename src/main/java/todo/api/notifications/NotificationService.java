package todo.api.notifications;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import todo.api.account.AccountErrorCode;
import todo.api.account.AccountRepository;
import todo.api.account.entity.Users;
import todo.api.common.exception.CustomException;
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
    private final RedisTemplate<String, Long> redisTemplate;
    private final String NOTIFICATION_KEY = "notifications";

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

        addSchedule(notification);

        return notification.getId();
    }

    public void sendNotification(Long notificationId) {
        Notifications notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> {
                    removeSchedule(notificationId);
                    return new RuntimeException("Notification not found");
                });

        // TODO: 이메일 전송, 푸시 알림 등
        log.info("---------------- 알림 : [{}] {}"
                , notification.getTodo().getTitle(), notification.getMessage());

        // 알림 전송 후 스케줄러에서 삭제
        removeSchedule(notificationId);
        // 반복 알림 처리
//        if (notification.getRepeatCount() > 0) {
//            notification.setRepeatCount(notification.getRepeatCount() - 1);
//            notification.setDueDate(calculateNextDueDate(notification));
//            notificationRepository.save(notification);
//            redisNotificationService.scheduleNotification(notification.getId(), notification.getDueDate());
//        }

    }

    private void addSchedule(Notifications notification) {
        double score = notification.getDueDate()
                .toInstant(ZoneOffset.UTC)
                .toEpochMilli();

        redisTemplate.opsForZSet()
                .add(NOTIFICATION_KEY, notification.getId(), score);
    }

    private void removeSchedule(Long notificationId) {
        redisTemplate.opsForZSet()
                .remove(NOTIFICATION_KEY, notificationId);
    }
}
