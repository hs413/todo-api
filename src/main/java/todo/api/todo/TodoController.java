package todo.api.todo;

import todo.api.auth.CustomPrincipal;
import todo.api.auth.UserInfo;
import todo.api.common.exception.BindingResultHandler;
import todo.api.todo.entity.request.TodoCreateReq;
import todo.api.todo.entity.request.TodoListReq;
import todo.api.todo.entity.request.TodoUpdateReq;
import todo.api.todo.entity.response.TodoRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    @Operation(summary = "할일 등록", description = "새로운 할일을 등록합니다.")
    @ApiResponse(responseCode = "200")
    public void createTodo(
            @Parameter(hidden = true) @CustomPrincipal UserInfo userInfo,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "할일 생성 요청",
                    required = true,
                    content = @Content(schema = @Schema(implementation = TodoCreateReq.class))
            )
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
    @Operation(summary = "할일 목록 조회", description = "할일 목록을 조회합니다.")
    @ApiResponse(responseCode = "200",
            content = @Content(schema = @Schema(implementation = TodoRes[].class)))
    public Page<TodoRes> getListTodo(
            @Parameter(hidden = true) @CustomPrincipal UserInfo userInfo,
            @ParameterObject TodoListReq req,
            @ParameterObject Pageable pageable
    ) {
        return todoService.todoList(userInfo.id(), pageable, req);
    }

    @GetMapping("{todoId}")
    @Operation(summary = "할일 상세 조회", description = "할일의 상세 정보를 조회합니다.")
    @ApiResponse(responseCode = "200",
            content = @Content(schema = @Schema(implementation = TodoRes.class)))
    public TodoRes getDetailTodo(
            @Parameter(hidden = true) @CustomPrincipal UserInfo userInfo,
            @PathVariable Long todoId
    ) {
        return todoService.todoDetail(userInfo.id(), todoId);
    }

    @PutMapping("{todoId}")
    @Operation(summary = "할일 수정", description = "할일의 정보를 수정합니다.")
    @ApiResponse(responseCode = "200")
    public void updateTodo(
            @Parameter(hidden = true) @CustomPrincipal UserInfo userInfo,
            @PathVariable Long todoId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "할일 수정 요청",
                    required = true,
                    content = @Content(schema = @Schema(implementation = TodoUpdateReq.class))
            )
            @Valid @RequestBody TodoUpdateReq req,
            BindingResult bindingResult
    ) {
        BindingResultHandler.execute(bindingResult, List.of(
                TodoErrorCode.REQUIRED_TITLE,
                TodoErrorCode.REQUIRED_DESCRIPTION
        ));

        todoService.todoUpdate(userInfo.id(), todoId, req);
    }

    @DeleteMapping("{todoId}")
    @Operation(summary = "할일 삭제", description = "할일을 삭제합니다.")
    @ApiResponse(responseCode = "200")
    public void deleteTodo(
            @Parameter(hidden = true) @CustomPrincipal UserInfo userInfo,
            @PathVariable Long todoId
    ) {
        todoService.todoDelete(userInfo.id(), todoId);
    }
}
