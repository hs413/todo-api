package todo.api.todo.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.BindingResult;
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
import todo.api.todo.service.TodoService;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("todos/shared")
public class SharedController {

    private final TodoService todoService;

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

        log.info(req);
    }

//    @GetMapping
//    public void todo() {
//        todoService
//        todoService
//    }
}
