package todo.api.notifications;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import todo.api.auth.CustomPrincipal;
import todo.api.auth.UserInfo;
import todo.api.common.exception.BindingResultHandler;
import todo.api.notifications.entity.request.NotificationCreateReq;
import todo.api.notifications.service.NotificationService;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public void notificationCreate(
            @CustomPrincipal UserInfo userInfo,
            @Valid @RequestBody NotificationCreateReq req,
            BindingResult bindingResult
    ) {
        BindingResultHandler.execute(bindingResult, List.of(
                NotificationErrorCode.REQUIRED_DUE_DATE,
                NotificationErrorCode.REQUIRED_TODO
        ));

        notificationService.notificationCreate(userInfo.id(), req);
    }
}
