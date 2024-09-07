package todo.api.todo.entity.request;

import todo.api.todo.entity.TodosPriority;
import todo.api.todo.entity.TodosStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record TodoUpdateReq(
        @Schema(description = "할일 제목", maxLength = 100)
        @NotBlank(message = "REQUIRED_TITLE")
        String title,

        @Schema(description = "할일 설명", maxLength = 500)
        @NotBlank(message = "REQUIRED_DESCRIPTION")
        String description,

        @Schema(description = "할일 상태", defaultValue = "PENDING")
        TodosStatus status,

        @Schema(description = "우선순위", defaultValue = "MID")
        TodosPriority priority

) {

}
