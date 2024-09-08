package todo.api.notifications;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final RedisTemplate<String, Long> redisTemplate;
    private final NotificationService notificationService;
    private final String NOTIFICATION_KEY = "notifications";

    @Scheduled(fixedRate = 1000 * 5)
    public void checkAndSendNotifications() {
        long currentTime = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        Set<Long> notificationIds = redisTemplate.opsForZSet()
                .rangeByScore(NOTIFICATION_KEY, 0, currentTime);

        if (!notificationIds.isEmpty()) {
            for (Long notificationId : notificationIds) {
                notificationService.sendNotification(notificationId);
            }
        }
    }
}
