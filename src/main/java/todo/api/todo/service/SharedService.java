package todo.api.todo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import todo.api.account.AccountErrorCode;
import todo.api.account.AccountRepository;
import todo.api.account.entity.Users;
import todo.api.common.exception.CustomException;
import todo.api.todo.TodoErrorCode;
import todo.api.todo.entity.Todos;
import todo.api.todo.entity.TodosSharing;
import todo.api.todo.entity.request.SharedReq;
import todo.api.todo.repository.SharedRepository;
import todo.api.todo.repository.TodoRepository;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class SharedService {

    private final SharedRepository sharedRepository;
    private final AccountRepository accountRepository;
    private final TodoRepository todoRepository;

    public Long todoShare(Long userId, Long todoId, SharedReq req) {
        Todos todos = todoRepository.findById(todoId)
                .orElseThrow(() -> new CustomException(TodoErrorCode.NO_TODOS));

        if (!todos.getUser().getId().equals(userId)) {
            throw new CustomException(TodoErrorCode.NO_TODOS);
        }

        Users sharedUser = accountRepository.findByEmail(req.email())
                .orElseThrow(() -> new CustomException(AccountErrorCode.NO_USERS));

        TodosSharing entity = req.toEntity(sharedUser, todos);

        sharedRepository.save(entity);

        return entity.getId();
    }
}
