package todo.api.common.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    // 사용자 정의 예외
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();
        int statusValue = errorCode.getStatus().value();

        ErrorResponse errorResponse =
                new ErrorResponse(statusValue, errorCode.getMessage());

        log.error("사용자 정의 에러 : {}", e.getMessage(), e);
        return ResponseEntity
                .status(statusValue)
                .body(errorResponse);
    }


    // 모든 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        int statusValue = HttpStatus.INTERNAL_SERVER_ERROR.value();

        ErrorResponse errorResponse =
                new ErrorResponse(statusValue, e.getMessage());

        log.error("서버 에러 : {}", e.getMessage(), e);
        return ResponseEntity
                .status(statusValue)
                .body(errorResponse);
    }
}
