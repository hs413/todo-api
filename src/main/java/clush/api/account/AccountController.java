package clush.api.account;

import static clush.api.account.AccountErrorCode.INVALID_EMAIL_PATTERN;
import static clush.api.account.AccountErrorCode.INVALID_PASSWORD_PATTERN;
import static clush.api.account.AccountErrorCode.REQUIRED_EMAIL;
import static clush.api.account.AccountErrorCode.REQUIRED_NAME;
import static clush.api.account.AccountErrorCode.REQUIRED_PASSWORD;
import static clush.api.account.AccountErrorCode.REQUIRED_PASSWORD_CONFIRM;
import static clush.api.auth.TokenErrorCode.NO_ACCESS_TOKEN;
import static clush.api.auth.TokenErrorCode.NO_REFRESH_TOKEN;

import clush.api.account.entity.request.AccountCreateReq;
import clush.api.account.entity.request.LoginReq;
import clush.api.account.entity.request.TokenRefreshReq;
import clush.api.account.entity.response.LoginRes;
import clush.api.common.exception.BindingResultHandler;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("accounts")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public void createAccount(@Valid @RequestBody AccountCreateReq req,
            BindingResult bindingResult) {

        BindingResultHandler.execute(bindingResult, List.of(
                REQUIRED_NAME,
                REQUIRED_EMAIL,
                INVALID_EMAIL_PATTERN,
                REQUIRED_PASSWORD,
                INVALID_PASSWORD_PATTERN,
                REQUIRED_PASSWORD_CONFIRM));

        accountService.createAccount(req);
    }

    @PostMapping("login")
    public LoginRes login(@Valid @RequestBody LoginReq req,
            BindingResult bindingResult) {

        BindingResultHandler.execute(bindingResult, List.of(REQUIRED_EMAIL, REQUIRED_PASSWORD));

        return accountService.login(req);
    }

    @PostMapping("token-refresh")
    public LoginRes tokenRefresh(@Valid @RequestBody TokenRefreshReq req,
            BindingResult bindingResult) {
        BindingResultHandler.execute(bindingResult, List.of(NO_ACCESS_TOKEN, NO_REFRESH_TOKEN));

        return accountService.tokenRefresh(req);
    }
}
