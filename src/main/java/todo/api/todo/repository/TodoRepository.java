package todo.api.todo.repository;

import todo.api.todo.entity.Todos;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todos, Long>, TodoCustomRepository {

    List<Todos> findAllByUserId(Long userId, Pageable pageable);

}