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
    REQUIRED_MESSAGE(BAD_REQUEST, "알림 메시지를 입력해주세요"),
    REQUIRED_TODO(BAD_REQUEST, "할일을 지정해주세요"),
    NOT_BEFORE_NOW(BAD_REQUEST, "현재 날짜보다 이전으로 설정할 수 없습니다."),
    ALREADY_CREATED(BAD_REQUEST, "이미 알림이 등록되었습니다."),
    NO_NOTIFICATION(BAD_REQUEST, "등록된 알림이 없습니다.");

    private final HttpStatus status;
    private final String message;

    public String getCode() {
        return this.name();
    }
}
