package todo.api.todo.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import todo.api.todo.entity.Todos;

public interface TodoRepository extends JpaRepository<Todos, Long>, TodoCustomRepository {

    @Query("SELECT t, ts.sharedUser.id, ts.permission " +
            "FROM Todos t " +
            "LEFT JOIN TodosSharing ts ON t.id = ts.todos.id " +
            "WHERE (t.id = :todoId AND t.user.id = :userId) " +
            "   OR (ts.sharedUser.id = :userId AND t.id = :todoId)")
    Optional<Todos> findByIdOrShared(@Param("userId") Long userId, @Param("todoId") Long todoId);
}
