package clush.api.todo.repository;

import clush.api.todo.entity.request.TodoListReq;
import clush.api.todo.entity.response.TodoRes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TodoCustomRepository {

    Page<TodoRes> findTodoList(Long userId, Pageable pageable, TodoListReq req);
}
