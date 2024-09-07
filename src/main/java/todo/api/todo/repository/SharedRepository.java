package todo.api.todo.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import todo.api.todo.entity.TodosSharing;

public interface SharedRepository extends JpaRepository<TodosSharing, Long>, TodoCustomRepository {

    boolean existsBySharedUserIdAndTodosId(Long sharedUserId, Long todoId);

    Optional<TodosSharing> findBySharedUserIdAndTodosId(Long sharedUserId, Long todoId);

}
