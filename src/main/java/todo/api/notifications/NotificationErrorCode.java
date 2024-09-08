package todo.api.notifications;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import todo.api.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements ErrorCode {
    REQUIRED_DUE_DATE(BAD_REQUEST, "알림 날짜를 입력해주세요"),
    REQUIRED_TODO(BAD_REQUEST, "할일을 지정해주세요"),
    ;

    private final HttpStatus status;
    private final String message;

    public String getCode() {
        return this.name();
    }
}
