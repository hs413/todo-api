package clush.api.todo;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import clush.api.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TodoErrorCode implements ErrorCode {
    REQUIRED_TITLE(BAD_REQUEST, "제목을 입력해주세요."),
    REQUIRED_DESCRIPTION(BAD_REQUEST, "설명을 입력해주세요."),
    ;

    private final HttpStatus status;
    private final String message;

    public String getCode() {
        return this.name();
    }
}
