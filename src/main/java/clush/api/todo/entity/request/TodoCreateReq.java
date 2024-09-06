package clush.api.todo.entity.request;

import clush.api.account.entity.Users;
import clush.api.todo.entity.Todos;
import clush.api.todo.entity.TodosPriority;
import clush.api.todo.entity.TodosStatus;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public record TodoCreateReq(
        @NotBlank(message = "REQUIRED_TITLE")
        String title,

        @NotBlank(message = "REQUIRED_DESCRIPTION")
        String description,

        TodosStatus status,

        TodosPriority priority,

        LocalDateTime dueDate
) {

    public Todos toEntity(Users user) {
        return Todos.builder()
                .user(user)
                .title(title)
                .description(description)
                .priority(priority == null ? TodosPriority.MEDIUM : priority)
                .status(status == null ? TodosStatus.PENDING : status)
                .dueDate(dueDate)
                .build();
    }
}
