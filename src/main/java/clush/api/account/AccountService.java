package clush.api.account;

import clush.api.account.entity.Users;
import clush.api.account.entity.request.AccountCreateReq;
import clush.api.account.entity.request.LoginReq;
import clush.api.account.entity.request.TokenRefreshReq;
import clush.api.account.entity.response.LoginRes;
import clush.api.auth.JwtUtil;
import clush.api.auth.TokenErrorCode;
import clush.api.common.exception.CustomException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.Date;
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

    public LoginRes tokenRefresh(TokenRefreshReq req) {

        checkAccessToken(req.accessToken());

        Map<String, Object> refreshClaims = checkRefreshToken(req.refreshToken());

        String id = (String) refreshClaims.get("id");
        String username = (String) refreshClaims.get("username");
        Map<String, Object> claim = Map.of("id", id, "username", username);

        String accessToken = jwtUtil.generateToken(claim, 1);

        // 만료 시간과 현재 시간의 간격이 3일 미만인 경우 Refresh 토큰 다시 생성
        String refreshToken = req.refreshToken();
        Long exp = (Long) refreshClaims.get("exp");
        Date expTime = new Date(Instant.ofEpochMilli(exp).toEpochMilli() * 1000);
        Date current = new Date(System.currentTimeMillis());
        long gapTime = (expTime.getTime() - current.getTime());

        if (gapTime < (1000 * 60 * 60 * 24 * 3)) {
            refreshToken = jwtUtil.generateToken(claim, 14);
        }

        return new LoginRes(accessToken, refreshToken, username);
    }

    private void checkAccessToken(String accessToken) {
        try {
            jwtUtil.validateToken(accessToken);
        } catch (ExpiredJwtException expiredJwtException) {
            log.info("Access Token has expired");
        } catch (Exception exception) {
            throw new CustomException(TokenErrorCode.MALFORMED_TOKEN);
        }
    }

    private Map<String, Object> checkRefreshToken(String refreshPath) {
        try {
            return jwtUtil.validateToken(refreshPath);
        } catch (ExpiredJwtException expiredJwtException) {
            throw new CustomException(TokenErrorCode.EXPIRED_TOKEN);
        } catch (MalformedJwtException malformedJwtException) {
            throw new CustomException(TokenErrorCode.MALFORMED_TOKEN);
        } catch (Exception exception) {
            throw new CustomException(TokenErrorCode.MALFORMED_TOKEN);
        }
    }
}
