package todo.api.notifications.entity.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import todo.api.account.entity.Users;
import todo.api.notifications.entity.Notifications;
import todo.api.notifications.entity.RepeatUnit;
import todo.api.todo.entity.Todos;

public record NotificationCreateReq(
        @NotNull(message = "REQUIRED_DUE_DATE")
        LocalDateTime dueDate,

        @NotNull(message = "REQUIRED_TODO")
        Long todoId,

        Integer repeatCount,

        Integer repeatInterval,

        RepeatUnit repeatUnit

) {

    public Notifications toEntity(Users user, Todos todos) {
        return Notifications.builder()
                .user(user)
                .dueDate(dueDate)
                .repeatCount(repeatCount)
                .repeatInterval(repeatInterval)
                .repeatUnit(repeatUnit)
                .todo(todos)
                .build();
    }
}
