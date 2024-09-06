package clush.api.todo;

import clush.api.account.entity.Users;
import clush.api.todo.entity.Todos;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TodoRepository extends JpaRepository<Todos, Long> {

    List<Todos> findAllByUserId(Long userId, Pageable pageable);

}
