package clush.api.common.exception;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.validation.BindingResult;

public class BindingResultHandler {

    public static void execute(BindingResult bindingResult, List<ErrorCode> errorCodes) {
        if (!bindingResult.hasErrors()) {
            return;
        }

        Set<String> errorMessages = new HashSet<>();

        bindingResult.getFieldErrors().forEach(fieldError -> {
            errorMessages.add(fieldError.getDefaultMessage());
        });

        errorCodes.forEach(errorCode -> {
            if (errorMessages.contains(errorCode.getCode())) {
                throw new CustomException(errorCode);
            }
        });
    }
}
