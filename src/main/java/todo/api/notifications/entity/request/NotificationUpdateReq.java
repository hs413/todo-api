package todo.api.notifications.entity.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import todo.api.notifications.entity.RepeatUnit;

public record NotificationUpdateReq(
        @NotNull(message = "REQUIRED_DUE_DATE")
        LocalDateTime dueDate,

        @NotBlank(message = "REQUIRED_MESSAGE")
        String message,

        Integer repeatCount,

        Integer repeatInterval,

        RepeatUnit repeatUnit

) {

}
