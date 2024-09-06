package clush.api.todo.entity.response;

import clush.api.todo.entity.Todos;
import clush.api.todo.entity.TodosPriority;
import clush.api.todo.entity.TodosStatus;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;

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
