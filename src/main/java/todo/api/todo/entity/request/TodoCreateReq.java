package todo.api.todo.entity.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import todo.api.account.entity.Users;
import todo.api.todo.entity.Todos;
import todo.api.todo.entity.enums.TodosPriority;
import todo.api.todo.entity.enums.TodosStatus;

public record TodoCreateReq(
        @Schema(description = "할일 제목", maxLength = 100, required = true)
        @NotBlank(message = "REQUIRED_TITLE")
        String title,

        @Schema(description = "할일 설명", maxLength = 500, required = true)
        @NotBlank(message = "REQUIRED_DESCRIPTION")
        String description,

        @Schema(description = "할일 상태", defaultValue = "PENDING")
        TodosStatus status,

        @Schema(description = "우선순위", defaultValue = "MID")
        TodosPriority priority
) {

    public Todos toEntity(Users user) {
        return Todos.builder()
                .user(user)
                .title(title)
                .description(description)
                .priority(priority == null ? TodosPriority.MID : priority)
                .status(status == null ? TodosStatus.PENDING : status)
                .build();
    }
}
