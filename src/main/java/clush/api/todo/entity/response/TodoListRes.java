package clush.api.todo.entity.response;

import clush.api.todo.entity.Todos;
import clush.api.todo.entity.TodosPriority;
import clush.api.todo.entity.TodosStatus;
import java.time.LocalDateTime;

public record TodoListRes(
        Long id,
        String title,
        String description,
        TodosStatus status,
        TodosPriority priority,
        LocalDateTime dueDate
) {

    public TodoListRes(Todos todos) {
        this(
                todos.getId(),
                todos.getTitle(),
                todos.getDescription(),
                todos.getStatus(),
                todos.getPriority(),
                todos.getDueDate()
        );
    }

}
