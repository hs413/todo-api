package clush.api.account;

import clush.api.account.entity.Users;
import clush.api.account.entity.request.AccountCreateReq;
import clush.api.common.exception.CustomException;
import jakarta.transaction.Transactional;
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

    public Long createAccounts(AccountCreateReq req) {
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
}
