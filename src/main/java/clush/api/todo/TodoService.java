package clush.api.todo;

import clush.api.account.AccountErrorCode;
import clush.api.account.AccountRepository;
import clush.api.account.entity.Users;
import clush.api.common.exception.CustomException;
import clush.api.todo.entity.Todos;
import clush.api.todo.entity.request.TodoCreateReq;
import clush.api.todo.entity.response.TodoListRes;
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

    public List<TodoListRes> todoList(Long userId, Pageable pageable) {
        List<Todos> todos = todoRepository.findAllByUserId(userId, pageable);

        return todos.stream().map(TodoListRes::new).toList();
    }
}