package todo.api.todo.service;

import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import todo.api.account.AccountErrorCode;
import todo.api.account.AccountRepository;
import todo.api.account.entity.Users;
import todo.api.common.exception.CustomException;
import todo.api.todo.TodoErrorCode;
import todo.api.todo.entity.Todos;
import todo.api.todo.entity.TodosSharing;
import todo.api.todo.entity.enums.SharingPermission;
import todo.api.todo.entity.request.TodoCreateReq;
import todo.api.todo.entity.request.TodoListReq;
import todo.api.todo.entity.request.TodoUpdateReq;
import todo.api.todo.entity.response.TodoRes;
import todo.api.todo.repository.SharedRepository;
import todo.api.todo.repository.TodoRepository;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class TodoService {

    private final TodoRepository todoRepository;
    private final AccountRepository accountRepository;
    private final SharedRepository sharedRepository;

    public Long todoCreate(Long userId, TodoCreateReq req) {
        Users user = accountRepository.findById(userId)
                .orElseThrow(() -> new CustomException(AccountErrorCode.NO_USERS));

        Todos todos = req.toEntity(user);

        todoRepository.save(todos);
        return todos.getId();
    }

    public Page<TodoRes> todoList(Long userId, Pageable pageable, TodoListReq req) {
        return todoRepository.findTodoList(userId, pageable, req);
    }

    public TodoRes todoDetail(Long userId, Long todoId) {
        Todos todos = todoRepository.findById(todoId)
                .orElseThrow(() -> new CustomException(TodoErrorCode.NO_TODOS));

        if (!todos.getUser().getId().equals(userId)) {
            checkShared(userId, todoId);
        }

        return new TodoRes(todos);
    }

    public Long todoUpdate(Long userId, Long todoId, TodoUpdateReq req) {
        Todos todos = todoRepository.findById(todoId)
                .orElseThrow(() -> new CustomException(TodoErrorCode.NO_TODOS));

        if (!todos.getUser().getId().equals(userId)) {
            checkSharedAndEditable(userId, todoId);
        }

        todos.update(req);

        todoRepository.save(todos);

        return todos.getId();
    }

    public void todoDelete(Long userId, Long todoId) {
        Todos todos = todoRepository.findByIdAndUserId(todoId, userId)
                .orElseThrow(() -> new CustomException(TodoErrorCode.NO_TODOS));

        todoRepository.delete(todos);
    }

    private void checkSharedAndEditable(Long userId, Long todoId) {
        Optional<TodosSharing> shared = checkShared(userId, todoId);

        if (shared.isPresent() && !shared.get().getPermission()
                .equals(SharingPermission.EDITABLE)) {
            throw new CustomException(TodoErrorCode.NO_TODOS);
        }
    }

    private Optional<TodosSharing> checkShared(Long userId, Long todoId) {
        Optional<TodosSharing> shared = sharedRepository
                .findBySharedUserIdAndTodosId(userId, todoId);

        if (shared.isEmpty()) {
            throw new CustomException(TodoErrorCode.NO_TODOS);
        }
        return shared;
    }
}
