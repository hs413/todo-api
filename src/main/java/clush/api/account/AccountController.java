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
import clush.api.todo.entity.request.TodoUpdateReq;
import clush.api.todo.entity.response.TodoRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
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
    @SecurityRequirements
    @Operation(summary = "사용자 등록", description = "이름, 이메일, 비밀번호를 입력하여 사용자를 등록합니다. 비밀번호는 8 ~ 20자, 영어 + 숫자 + 조합으로 입력해야 합니다.")
    @ApiResponse(responseCode = "200")
    public void createAccount(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "사용자 등록",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AccountCreateReq.class))
            )
            @Valid @RequestBody AccountCreateReq req,
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
    @SecurityRequirements
    @Operation(summary = "로그인", description = "이메일과 비밀번호을 입력하여 로그인 합니다.")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = LoginRes.class)))
    public LoginRes login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "로그인",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginReq.class))
            )
            @Valid @RequestBody LoginReq req,
            BindingResult bindingResult) {

        BindingResultHandler.execute(bindingResult, List.of(REQUIRED_EMAIL, REQUIRED_PASSWORD));

        return accountService.login(req);
    }

    @PostMapping("token-refresh")
    @SecurityRequirements
    @Operation(summary = "토큰 재발급", description = "리프레시 토큰을 사용하여 토큰을 재발급 합니다.")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = LoginRes.class)))
    public LoginRes tokenRefresh(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "토큰 재발급",
                    required = true,
                    content = @Content(schema = @Schema(implementation = TokenRefreshReq.class))
            )
            @Valid @RequestBody TokenRefreshReq req,
            BindingResult bindingResult) {
        BindingResultHandler.execute(bindingResult, List.of(NO_ACCESS_TOKEN, NO_REFRESH_TOKEN));

        return accountService.tokenRefresh(req);
    }
}
