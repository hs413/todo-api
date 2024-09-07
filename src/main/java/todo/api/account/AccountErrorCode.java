package todo.api.account;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import todo.api.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AccountErrorCode implements ErrorCode {
    DUPLICATED_EMAIL(BAD_REQUEST, "이미 가입된 이메일 주소입니다"),
    REQUIRED_EMAIL(BAD_REQUEST, "이메일 주소를 입력해 주세요"),
    INVALID_EMAIL_PATTERN(BAD_REQUEST, "이메일 형식이 아닙니다"),

    REQUIRED_PASSWORD(BAD_REQUEST, "비밀번호를 입력해 주세요"),
    INVALID_PASSWORD_PATTERN(BAD_REQUEST, "비밀번호는 8~20자 이내로 영어, 숫자, 특수문자를 포함해야 합니다"),
    REQUIRED_PASSWORD_CONFIRM(BAD_REQUEST, "비밀번호 확인이 입력되지 않았습니다"),
    DIFFERENT_PASSWORD_CONFIRM(BAD_REQUEST, "비밀번호 확인이 일치하지 않습니다"),

    REQUIRED_NAME(BAD_REQUEST, "이름이 입력되지 않았습니다"),
    NO_EMAIL_OR_PASSWORD(BAD_REQUEST, "아이디 또는 비밀번호가 잘못되었습니다. 다시 입력해 주세요"),
    NO_USERS(BAD_REQUEST, "존재하지 않는 사용자입니다."),
    ;

    private final HttpStatus status;
    private final String message;

    public String getCode() {
        return this.name();
    }
}
