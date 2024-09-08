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
import todo.api.notifications.entity.NotificationsHistory;
import todo.api.notifications.entity.RepeatUnit;
import todo.api.notifications.entity.request.NotificationCreateReq;
import todo.api.notifications.entity.request.NotificationUpdateReq;
import todo.api.notifications.entity.response.NotificationRes;
import todo.api.notifications.repository.NotificationHistoryRepository;
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
    private final NotificationHistoryRepository notificationHistoryRepository;
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

        addSchedule(notification.getId(), notification.getDueDate());

        return notification.getId();
    }

    public NotificationRes notificationDetail(Long userId, Long notificationId) {
        Notifications notification = notificationRepository
                .findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new CustomException(NotificationErrorCode.NO_NOTIFICATION));

        return new NotificationRes(notification);
    }

    public Long notificationUpdate(Long userId, Long notificationId, NotificationUpdateReq req) {
        Notifications notification = notificationRepository
                .findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new CustomException(NotificationErrorCode.NO_NOTIFICATION));

        if (req.dueDate().isBefore(LocalDateTime.now())) {
            throw new CustomException(NotificationErrorCode.NOT_BEFORE_NOW);
        }

        boolean changedDueDate = isDueDateChanged(notification.getDueDate(), req.dueDate());

        notification.update(req);
        notificationRepository.save(notification);

        if (changedDueDate) {
            removeSchedule(notification.getId());
            addSchedule(notification.getId(), notification.getDueDate());
        }

        return notification.getId();
    }

    public void notificationDelete(Long userId, Long notificationId) {
        Notifications notification = notificationRepository
                .findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new CustomException(NotificationErrorCode.NO_NOTIFICATION));

        notificationRepository.delete(notification);
        removeSchedule(notification.getId());
    }

    public void sendNotification(Long notificationId) {
        Notifications notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> {
                    removeSchedule(notificationId);
                    return new CustomException(NotificationErrorCode.NO_NOTIFICATION);
                });

        // TODO: 이메일 발송, 푸시 알림 등
        log.info("---------------- 알림 : [{}] {}"
                , notification.getTodo().getTitle(), notification.getMessage());

        // 알림 발송 history 등록
        NotificationsHistory history = NotificationsHistory.builder()
                .notifications(notification)
                .version(notification.getVersion())
                .build();

        notificationHistoryRepository.save(history);

        // 알림 발송 후 스케줄러에서 삭제
        removeSchedule(notificationId);

        // 알림 반복 처리
        Long sentCount = notificationHistoryRepository
                .sentCount(notificationId, notification.getVersion());
        if (notification.getRepeatCount() > sentCount - 1) {
            log.info("----------------------- repeat");
            LocalDateTime nextDueDate = calculateNextDueDate(notification);

            addSchedule(notification.getId(), nextDueDate);
        }
    }

    private LocalDateTime calculateNextDueDate(Notifications notification) {
        LocalDateTime dueDate = notification.getDueDate();
        Integer interval = notification.getRepeatInterval();
        RepeatUnit repeatUnit = notification.getRepeatUnit();

        switch (repeatUnit) {
            case MINUTES:
                return dueDate.plusMinutes(interval);
            case HOUR:
                return dueDate.plusHours(interval);
            default:
                return dueDate.plusDays(interval);
        }
    }

    private boolean isDueDateChanged(LocalDateTime originDueDate, LocalDateTime dueDate) {
        return !originDueDate.isEqual(dueDate.withSecond(0).withNano(0));
    }

    private void addSchedule(Long notificationId, LocalDateTime dueDate) {
        double score = dueDate
                .toInstant(ZoneOffset.UTC)
                .toEpochMilli();

        redisTemplate.opsForZSet()
                .add(NOTIFICATION_KEY, notificationId, score);
    }

    private void removeSchedule(Long notificationId) {
        redisTemplate.opsForZSet()
                .remove(NOTIFICATION_KEY, notificationId);
    }
}
