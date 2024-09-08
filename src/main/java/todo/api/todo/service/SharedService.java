package todo.api.todo.service;

import jakarta.transaction.Transactional;
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
import todo.api.todo.entity.request.SharedReq;
import todo.api.todo.entity.request.TodoListReq;
import todo.api.todo.entity.response.TodoRes;
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
        Todos todos = todoRepository.findByIdAndUserId(todoId, userId)
                // 본인이 등록한 할일이 아닌 경우 예외처리
                .orElseThrow(() -> new CustomException(TodoErrorCode.NO_TODOS));

        // 유저가 존재하지 않는 경우 예외처리
        Users sharedUser = accountRepository.findByEmail(req.email())
                .orElseThrow(() -> new CustomException(AccountErrorCode.NO_USERS));

        boolean exists = sharedRepository
                .existsBySharedUserIdAndTodosId(sharedUser.getId(), todoId);

        // 이미 공유된 경우 예외처리
        if (exists) {
            throw new CustomException(TodoErrorCode.ALREADY_SHARED);
        }

        TodosSharing shared = req.toEntity(sharedUser, todos);
        sharedRepository.save(shared);
        return shared.getId();
    }

    public Page<TodoRes> sharedList(Long userId, Pageable pageable, TodoListReq req) {
        return sharedRepository.findSharedList(userId, pageable, req);
    }

    public void sharingCancel(Long userId, Long todoId, SharedReq req) {
        Todos todos = todoRepository.findByIdAndUserId(todoId, userId)
                // 본인이 등록한 할일이 아닌 경우 예외처리
                .orElseThrow(() -> new CustomException(TodoErrorCode.NO_TODOS));

        // 유저가 존재하지 않는 경우 예외처리
        Users sharedUser = accountRepository.findByEmail(req.email())
                .orElseThrow(() -> new CustomException(AccountErrorCode.NO_USERS));

        TodosSharing shared = sharedRepository
                .findBySharedUserIdAndTodosId(sharedUser.getId(), todoId)
                .orElseThrow(() -> new CustomException(TodoErrorCode.NO_SHARED));

        sharedRepository.delete(shared);
    }
}
