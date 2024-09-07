package todo.api.todo.entity.response;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import todo.api.todo.entity.Todos;
import todo.api.todo.entity.enums.TodosPriority;
import todo.api.todo.entity.enums.TodosStatus;

public record TodoRes(
        Long id,
        String title,
        String description,
        TodosStatus status,
        TodosPriority priority,
        LocalDateTime createdAt
) {

    public TodoRes(Todos todos) {
        this(
                todos.getId(),
                todos.getTitle(),
                todos.getDescription(),
                todos.getStatus(),
                todos.getPriority(),
                todos.getCreatedAt()
        );
    }

    @QueryProjection
    public TodoRes {

    }

}
