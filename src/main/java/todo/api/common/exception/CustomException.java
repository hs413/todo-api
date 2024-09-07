package todo.api.common.exception;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import lombok.Getter;
import org.springframework.http.MediaType;

@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public void sendResponseError(HttpServletResponse response) {
        response.setStatus(getErrorCode().getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> map = Map.of(
                "status", getErrorCode().getStatus().value(),
                "message", getErrorCode().getMessage());

        Gson gson = new Gson();
        try {
            gson.toJson(map, response.getWriter());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
