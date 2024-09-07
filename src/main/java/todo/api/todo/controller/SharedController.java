package todo.api.todo.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import todo.api.auth.CustomPrincipal;
import todo.api.auth.UserInfo;
import todo.api.common.exception.BindingResultHandler;
import todo.api.todo.TodoErrorCode;
import todo.api.todo.entity.request.SharedReq;
import todo.api.todo.entity.request.TodoListReq;
import todo.api.todo.entity.response.TodoRes;
import todo.api.todo.service.SharedService;
import todo.api.todo.service.TodoService;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("todos/shared")
public class SharedController {

    private final TodoService todoService;
    private final SharedService sharedService;

    @PostMapping("{todoId}")
    public void todoShare(
            @CustomPrincipal UserInfo userInfo,
            @PathVariable Long todoId,
            @RequestBody SharedReq req,
            BindingResult bindingResult
    ) {
        BindingResultHandler.execute(bindingResult, List.of(
                TodoErrorCode.REQUIRED_EMAIL,
                TodoErrorCode.INVALID_EMAIL_PATTERN
        ));

        sharedService.todoShare(userInfo.id(), todoId, req);
    }

    @GetMapping
    public Page<TodoRes> sharedList(
            @CustomPrincipal UserInfo userInfo,
            @ParameterObject TodoListReq req,
            @ParameterObject Pageable pageable
    ) {
        return sharedService.sharedList(userInfo.id(), pageable, req);
    }
}
