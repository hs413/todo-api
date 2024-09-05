package clush.api.account;

import clush.api.account.entity.Users;
import clush.api.account.entity.request.AccountCreateReq;
import clush.api.account.entity.request.LoginReq;
import clush.api.account.entity.response.LoginRes;
import clush.api.auth.JwtUtil;
import clush.api.common.exception.CustomException;
import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public Long createAccount(AccountCreateReq req) {
        if (!req.password().equals(req.passwordConfirm())) {
            throw new CustomException(AccountErrorCode.DIFFERENT_PASSWORD_CONFIRM);
        }

        boolean exits = accountRepository.existsByEmail(req.email());

        if (exits) {
            throw new CustomException(AccountErrorCode.DUPLICATED_EMAIL);
        }

        String password = passwordEncoder.encode(req.password());
        Users user = req.toEntity(password);

        accountRepository.save(user);
        return user.getId();
    }

    public LoginRes login(LoginReq req) {
        Users users = accountRepository.findByEmail(req.email())
                .orElseThrow(() -> new CustomException(AccountErrorCode.NO_EMAIL_OR_PASSWORD));

        if (!passwordEncoder.matches(req.password(), users.getPassword())) {
            throw new CustomException(AccountErrorCode.NO_EMAIL_OR_PASSWORD);
        }

        Map<String, Object> claims = new HashMap<>();

        claims.put("id", users.getId().toString());
        claims.put("username", users.getUsername());

        String accessToken = jwtUtil.generateToken(claims, 1);
        String refreshToken = jwtUtil.generateToken(claims, 14);

        LoginRes response = new LoginRes(
                accessToken,
                refreshToken,
                users.getUsername());

        return response;
    }
}
