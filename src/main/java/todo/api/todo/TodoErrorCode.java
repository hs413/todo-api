package todo.api.todo;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import todo.api.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TodoErrorCode implements ErrorCode {
    REQUIRED_TITLE(BAD_REQUEST, "제목을 입력해주세요."),
    REQUIRED_DESCRIPTION(BAD_REQUEST, "설명을 입력해주세요."),
    NO_TODOS(BAD_REQUEST, "할일이 존재하지 않습니다."),
    NO_PERMISSION(BAD_REQUEST, "권한이 없습니다."),
    ;

    private final HttpStatus status;
    private final String message;

    public String getCode() {
        return this.name();
    }
}
