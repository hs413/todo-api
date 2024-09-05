package clush.api.account;

import static clush.api.account.AccountErrorCode.INVALID_EMAIL_PATTERN;
import static clush.api.account.AccountErrorCode.INVALID_PASSWORD_PATTERN;
import static clush.api.account.AccountErrorCode.REQUIRED_EMAIL;
import static clush.api.account.AccountErrorCode.REQUIRED_NAME;
import static clush.api.account.AccountErrorCode.REQUIRED_PASSWORD;
import static clush.api.account.AccountErrorCode.REQUIRED_PASSWORD_CONFIRM;

import clush.api.account.entity.request.AccountCreateReq;
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

        accountService.createAccounts(req);
    }

}
