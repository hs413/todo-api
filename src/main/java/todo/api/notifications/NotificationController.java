package todo.api.notifications;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import todo.api.auth.CustomPrincipal;
import todo.api.auth.UserInfo;
import todo.api.common.exception.BindingResultHandler;
import todo.api.notifications.entity.request.NotificationCreateReq;
import todo.api.notifications.entity.request.NotificationListReq;
import todo.api.notifications.entity.request.NotificationUpdateReq;
import todo.api.notifications.entity.response.NotificationListRes;
import todo.api.notifications.entity.response.NotificationRes;

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
                NotificationErrorCode.REQUIRED_MESSAGE,
                NotificationErrorCode.REQUIRED_TODO
        ));

        notificationService.notificationCreate(userInfo.id(), req);
    }

    @GetMapping
    public Page<NotificationListRes> getNotifications(
            @CustomPrincipal UserInfo userInfo,
            @ModelAttribute NotificationListReq req,
            Pageable pageable
    ) {
        return notificationService.notificationList(userInfo.id(), pageable, req);
    }

    @GetMapping("{notificationId}")
    public NotificationRes notificationDetail(
            @CustomPrincipal UserInfo userInfo,
            @PathVariable Long notificationId
    ) {
        return notificationService.notificationDetail(userInfo.id(), notificationId);
    }

    @PutMapping("{notificationId}")
    public void notificationUpdate(
            @CustomPrincipal UserInfo userInfo,
            @PathVariable Long notificationId,
            @Valid @RequestBody NotificationUpdateReq req,
            BindingResult bindingResult
    ) {
        BindingResultHandler.execute(bindingResult, List.of(
                NotificationErrorCode.REQUIRED_DUE_DATE,
                NotificationErrorCode.REQUIRED_MESSAGE
        ));

        notificationService.notificationUpdate(userInfo.id(), notificationId, req);
    }

    @DeleteMapping("{notificationId}")
    public void notificationDelete(
            @CustomPrincipal UserInfo userInfo,
            @PathVariable Long notificationId
    ) {
        notificationService.notificationDelete(userInfo.id(), notificationId);
    }
}
