package todo.api.account;

import todo.api.account.entity.Users;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AccountRepository extends JpaRepository<Users, Long> {

    boolean existsByEmail(String email);

    Optional<Users> findByEmail(String email);
}
