package todo.api.todo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import todo.api.todo.entity.request.TodoListReq;
import todo.api.todo.entity.response.TodoRes;

public interface TodoCustomRepository {

    Page<TodoRes> findTodoList(Long userId, Pageable pageable, TodoListReq req);

    Page<TodoRes> findSharedList(Long userId, Pageable pageable, TodoListReq req);
}
