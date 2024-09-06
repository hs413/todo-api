package clush.api.todo;

import static org.assertj.core.api.Assertions.assertThat;

import clush.api.account.entity.Users;
import clush.api.todo.entity.Todos;
import clush.api.todo.entity.TodosPriority;
import clush.api.todo.entity.TodosStatus;
import clush.api.todo.entity.request.TodoCreateReq;
import clush.api.todo.entity.request.TodoUpdateReq;
import clush.api.todo.entity.response.TodoRes;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;

@SpringBootTest
@Log4j2
@Transactional
class TodoServiceTest {

    @Autowired
    private TodoService todoService;

    @PersistenceContext
    private EntityManager em;

    Users user;

    @BeforeEach
    void setUp() {
        user = Users.builder()
                .username("유저1")
                .email("email@example.com")
                .password("1234")
                .build();
        em.persist(user);
        em.flush();
        em.clear();
    }

    @Test
    public void 할일_생성() {
        // give
        TodoCreateReq req = new TodoCreateReq("todo 1", "todo todo",
                null, null, null);

        // when
        Long todoId = todoService.todoCreate(user.getId(), req);

        // then
        Todos todos = em.find(Todos.class, todoId);
        assertThat(todos.getTitle()).isEqualTo("todo 1");
    }

    @Test
    public void 할일_리스트() {
        for (int i = 1; i <= 12; i++) {
            Todos todo = Todos.builder()
                    .title("todo " + i)
                    .description("todo todo " + i)
                    .status(TodosStatus.PENDING)
                    .priority(TodosPriority.MEDIUM)
                    .user(user)
                    .build();
            em.persist(todo);
        }
        em.flush();
        em.clear();

        // give
        PageRequest pageRequest = PageRequest.of(0, 10, Direction.DESC, "id");

        // when
        List<TodoRes> list = todoService.todoList(user.getId(), pageRequest);

        // then
        assertThat(list).hasSize(10);
    }

    @Test
    public void 할일_상세() {
        // give
        Todos todo = Todos.builder()
                .title("todo 1")
                .description("todo todo 1")
                .status(TodosStatus.PENDING)
                .priority(TodosPriority.MEDIUM)
                .user(user)
                .build();
        em.persist(todo);
        em.flush();
        em.clear();

        // when
        TodoRes res = todoService.todoDetail(user.getId(), todo.getId());

        // then
        assertThat(res).isNotNull();
        assertThat(res.title()).isEqualTo("todo 1");
        assertThat(res.description()).isEqualTo("todo todo 1");
        assertThat(res.status()).isEqualTo(TodosStatus.PENDING);

    }

    @Test
    public void 할일_수정() {
        // give
        Todos todo = Todos.builder()
                .title("todo 1")
                .description("todo todo 1")
                .status(TodosStatus.PENDING)
                .priority(TodosPriority.MEDIUM)
                .user(user)
                .build();
        em.persist(todo);
        em.flush();
        em.clear();

        // when
        TodoUpdateReq req = new TodoUpdateReq("todo 2", "todo todo 2",
                TodosStatus.IN_PROGRESS, TodosPriority.HIGH, null);

        Long todoId = todoService.todoUpdate(user.getId(), todo.getId(), req);

        // then
        Todos todos = em.find(Todos.class, todoId);
        assertThat(todos.getTitle()).isEqualTo("todo 2");
        assertThat(todos.getDescription()).isEqualTo("todo todo 2");
        assertThat(todos.getStatus()).isEqualTo(TodosStatus.IN_PROGRESS);
        assertThat(todos.getPriority()).isEqualTo(TodosPriority.HIGH);
    }

    @Test
    public void 할일_삭제() {
        Todos todo = Todos.builder()
                .title("todo 1")
                .description("todo todo 1")
                .status(TodosStatus.PENDING)
                .priority(TodosPriority.MEDIUM)
                .user(user)
                .build();
        em.persist(todo);
        em.flush();
        em.clear();

        todoService.todoDelete(user.getId(), todo.getId());

        Todos todos = em.find(Todos.class, todo.getId());

        assertThat(todos).isNull();
    }
}