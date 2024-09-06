package clush.api.todo;

import clush.api.account.AccountErrorCode;
import clush.api.account.AccountRepository;
import clush.api.account.entity.Users;
import clush.api.common.exception.CustomException;
import clush.api.todo.entity.Todos;
import clush.api.todo.entity.request.TodoCreateReq;
import clush.api.todo.entity.request.TodoUpdateReq;
import clush.api.todo.entity.response.TodoRes;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class TodoService {

    private final TodoRepository todoRepository;
    private final AccountRepository accountRepository;

    public Long todoCreate(Long userId, TodoCreateReq req) {
        Users user = accountRepository.findById(userId)
                .orElseThrow(() -> new CustomException(AccountErrorCode.NO_USERS));

        Todos todos = req.toEntity(user);

        todoRepository.save(todos);
        return todos.getId();
    }

    public List<TodoRes> todoList(Long userId, Pageable pageable) {
        List<Todos> todos = todoRepository.findAllByUserId(userId, pageable);

        return todos.stream().map(TodoRes::new).toList();
    }

    public TodoRes todoDetail(Long userId, Long todoId) {
        Todos todos = todoRepository.findById(todoId)
                .orElseThrow(() -> new CustomException(TodoErrorCode.NO_TODOS));

        if (todos.getUser().getId() != userId) {
            throw new CustomException(TodoErrorCode.NO_TODOS);
        }

        return new TodoRes(todos);
    }

    public Long todoUpdate(Long userId, Long todoId, TodoUpdateReq req) {
        Todos todos = todoRepository.findById(todoId)
                .orElseThrow(() -> new CustomException(TodoErrorCode.NO_TODOS));

        if (todos.getUser().getId() != userId) {
            throw new CustomException(TodoErrorCode.NO_TODOS);
        }

        todos.update(req);

        todoRepository.save(todos);

        return todos.getId();
    }

    public void todoDelete(Long userId, Long todoId) {
        Todos todos = todoRepository.findById(todoId)
                .orElseThrow(() -> new CustomException(TodoErrorCode.NO_TODOS));

        if (todos.getUser().getId() != userId) {
            throw new CustomException(TodoErrorCode.NO_TODOS);
        }

        todoRepository.delete(todos);
    }
}
