package clush.api.todo.entity.request;

import clush.api.todo.entity.TodosPriority;
import clush.api.todo.entity.TodosStatus;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public record TodoUpdateReq(
        @NotBlank(message = "REQUIRED_TITLE")
        String title,

        @NotBlank(message = "REQUIRED_DESCRIPTION")
        String description,

        TodosStatus status,

        TodosPriority priority
) {

}
