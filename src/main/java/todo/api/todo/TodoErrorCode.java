package todo.api.todo;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import todo.api.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum TodoErrorCode implements ErrorCode {
    REQUIRED_TITLE(BAD_REQUEST, "제목을 입력해주세요."),
    REQUIRED_DESCRIPTION(BAD_REQUEST, "설명을 입력해주세요."),
    NO_TODOS(BAD_REQUEST, "할일이 존재하지 않습니다."),
    NO_PERMISSION(UNAUTHORIZED, "권한이 없습니다."),
    REQUIRED_EMAIL(BAD_REQUEST, "할일을 공유할 유저의 이메일 주소를 입력해 주세요"),
    INVALID_EMAIL_PATTERN(BAD_REQUEST, "이메일 형식이 아닙니다"),
    ALREADY_SHARED(BAD_REQUEST, "이미 공유되었습니다."),
    ;

    private final HttpStatus status;
    private final String message;

    public String getCode() {
        return this.name();
    }
}
