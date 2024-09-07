package todo.api.auth;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import todo.api.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TokenErrorCode implements ErrorCode {
    MALFORMED_TOKEN(FORBIDDEN, "잘못된 토큰입니다."),
    EXPIRED_TOKEN(FORBIDDEN, "토큰이 만료되었습니다."),
    NO_ACCESS_TOKEN(BAD_REQUEST, "액세스 토큰이 없습니다."),
    NO_REFRESH_TOKEN(BAD_REQUEST, "리프레시 토큰이 없습니다."),
    ;

    private final HttpStatus status;
    private final String message;

    public String getCode() {
        return this.name();
    }
}
