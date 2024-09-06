package clush.api.todo;

import clush.api.auth.CustomPrincipal;
import clush.api.auth.UserInfo;
import clush.api.common.exception.BindingResultHandler;
import clush.api.todo.entity.request.TodoCreateReq;
import clush.api.todo.entity.request.TodoUpdateReq;
import clush.api.todo.entity.response.TodoRes;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("todos")
public class TodoController {

    private final TodoService todoService;


    @PostMapping
    public void createTodo(
            @CustomPrincipal UserInfo userInfo,
            @Valid @RequestBody TodoCreateReq req,
            BindingResult bindingResult
    ) {
        BindingResultHandler.execute(bindingResult, List.of(
                TodoErrorCode.REQUIRED_TITLE,
                TodoErrorCode.REQUIRED_DESCRIPTION
        ));

        todoService.todoCreate(userInfo.id(), req);
    }

    @GetMapping
    public List<TodoRes> getListTodo(
            @CustomPrincipal UserInfo userInfo,
            Pageable pageable
    ) {
        return todoService.todoList(userInfo.id(), pageable);
    }

    @GetMapping("{todoId}")
    public TodoRes getDetailTodo(
            @CustomPrincipal UserInfo userInfo,
            @PathVariable Long todoId
    ) {
        return todoService.todoDetail(userInfo.id(), todoId);
    }

    @PutMapping("{todoId}")
    public void updateTodo(
            @CustomPrincipal UserInfo userInfo,
            @PathVariable Long todoId,
            @Valid @RequestBody TodoUpdateReq req,
            BindingResult bindingResult
    ) {
        BindingResultHandler.execute(bindingResult, List.of(
                TodoErrorCode.REQUIRED_TITLE,
                TodoErrorCode.REQUIRED_DESCRIPTION
        ));

        todoService.todoUpdate(userInfo.id(), todoId, req);
    }
}
