package clush.api.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import clush.api.account.entity.Users;
import clush.api.account.entity.request.AccountCreateReq;
import clush.api.account.entity.request.LoginReq;
import clush.api.account.entity.response.LoginRes;
import clush.api.common.exception.CustomException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@Log4j2
@SpringBootTest
@Transactional
class AccountServiceTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager em;

    @Test
    public void 계정생성() {
        // give
        AccountCreateReq req = new AccountCreateReq("유저1", "email@example.com",
                "qwer1234!", "qwer1234!");

        // when
        Long id = accountService.createAccount(req);
        em.flush();
        em.clear();

        // then
        Users user = em.find(Users.class, id);
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo("유저1");
        assertThat(user.getEmail()).isEqualTo("email@example.com");
        assertThat(passwordEncoder.matches("qwer1234!", user.getPassword()))
                .isEqualTo(true);
    }

    @Test
    public void 비밀번호확인_불일치() {
        // give
        AccountCreateReq req =
                new AccountCreateReq("유저1", "email@example.com", "qwer1234!", "qwer1234!!");

        // when
        CustomException ex = assertThrows(CustomException.class, () -> {
            accountService.createAccount(req);
        });

        // then
        assertThat(ex.getErrorCode()).isEqualTo(AccountErrorCode.DIFFERENT_PASSWORD_CONFIRM);
    }

    @Test
    public void 이메일_중복() {
        // give
        Users user = Users.builder()
                .username("유저1")
                .email("email@example.com")
                .password(passwordEncoder.encode("qwer1234!"))
                .build();
        em.persist(user);
        em.flush();
        em.clear();

        AccountCreateReq req =
                new AccountCreateReq("유저1", "email@example.com", "qwer1234!", "qwer1234!");

        // when
        CustomException ex = assertThrows(CustomException.class, () -> {
            accountService.createAccount(req);
        });

        // then
        assertThat(ex.getErrorCode()).isEqualTo(AccountErrorCode.DUPLICATED_EMAIL);
    }

    @Test
    public void 로그인() {
        // give
        Users user = Users.builder()
                .username("유저1")
                .email("email@example.com")
                .password(passwordEncoder.encode("qwer1234!"))
                .build();
        em.persist(user);
        em.flush();
        em.clear();

        LoginReq req = new LoginReq("email@example.com", "qwer1234!");

        // when
        LoginRes res = accountService.login(req);

        // then
        assertThat(res).isNotNull();
        assertThat(res.username()).isEqualTo("유저1");
    }
}