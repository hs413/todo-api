package todo.api.notifications.service;

import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import todo.api.notifications.entity.Notifications;

@Service
@RequiredArgsConstructor
public class NotificationRedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void scheduleNotification(Notifications notification) {
        String key = "notification:" + notification.getId();
        long delay = notification.getDueDate()
                .toInstant(ZoneOffset.UTC).toEpochMilli() - System.currentTimeMillis();

        redisTemplate.opsForValue()
                .set(key, notification.getId(), 3000, TimeUnit.MILLISECONDS);
    }

    public void cancelNotification(Long notificationId) {
        String key = "notification:" + notificationId;
        redisTemplate.delete(key);
    }
}
