package todo.api.todo;

import static org.assertj.core.api.Assertions.assertThat;

import todo.api.account.entity.Users;
import todo.api.todo.entity.Todos;
import todo.api.todo.entity.TodosPriority;
import todo.api.todo.entity.TodosStatus;
import todo.api.todo.entity.request.TodoCreateReq;
import todo.api.todo.entity.request.TodoListReq;
import todo.api.todo.entity.request.TodoUpdateReq;
import todo.api.todo.entity.response.TodoRes;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
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
                null, null);

        // when
        Long todoId = todoService.todoCreate(user.getId(), req);

        // then
        Todos todos = em.find(Todos.class, todoId);
        assertThat(todos.getTitle()).isEqualTo("todo 1");
    }

    @Nested
    @DisplayName("할일 상세 수정 삭제 테스트")
    class RUD {

        Todos todo;

        @BeforeEach
        void setUp() {
            todo = Todos.builder()
                    .title("todo 1")
                    .description("todo todo 1")
                    .status(TodosStatus.PENDING)
                    .priority(TodosPriority.MID)
                    .user(user)
                    .build();
            em.persist(todo);
            em.flush();
            em.clear();
        }

        @Test
        public void 상세() {
            // when
            TodoRes res = todoService.todoDetail(user.getId(), todo.getId());

            // then
            assertThat(res).isNotNull();
            assertThat(res.title()).isEqualTo("todo 1");
            assertThat(res.description()).isEqualTo("todo todo 1");
            assertThat(res.status()).isEqualTo(TodosStatus.PENDING);
        }

        @Test
        public void 수정() {
            // when
            TodoUpdateReq req = new TodoUpdateReq("todo 2", "todo todo 2",
                    TodosStatus.IN_PROGRESS, TodosPriority.HIGH);

            Long todoId = todoService.todoUpdate(user.getId(), todo.getId(), req);

            // then
            Todos todos = em.find(Todos.class, todoId);
            assertThat(todos.getTitle()).isEqualTo("todo 2");
            assertThat(todos.getDescription()).isEqualTo("todo todo 2");
            assertThat(todos.getStatus()).isEqualTo(TodosStatus.IN_PROGRESS);
            assertThat(todos.getPriority()).isEqualTo(TodosPriority.HIGH);
        }

        @Test
        public void 삭제() {
            // when
            todoService.todoDelete(user.getId(), todo.getId());

            // then
            Todos todos = em.find(Todos.class, todo.getId());
            assertThat(todos).isNull();
        }
    }

    @Nested
    @DisplayName("할일 리스트 테스트")
    class list {

        @BeforeEach
        public void setup() {
            for (int i = 1; i <= 12; i++) {
                Todos todo = Todos.builder()
                        .title("todo " + i)
                        .description("todo todo " + (13 - i))
                        .status(i % 2 == 1 ? TodosStatus.PENDING : TodosStatus.IN_PROGRESS)
                        .priority(TodosPriority.fromValue(i % 3 + 1))
                        .user(user)
                        .build();
                em.persist(todo);
            }
            em.flush();
            em.clear();
        }

        @Test
        public void 기본() {
            // give
            PageRequest pageRequest = PageRequest.of(0, 10);
            TodoListReq req = new TodoListReq(null, null, null);

            // when
            Page<TodoRes> list = todoService.todoList(user.getId(), pageRequest, req);

            // then
            assertThat(list).hasSize(10);

            // 기본적으로 ID 내림차순 정렬 적용
            assertThat(list.getContent().get(0).title()).isEqualTo("todo 12");
        }

        @Test
        public void 정렬_우선순위_내림차순() {
            // give
            PageRequest pageRequest = PageRequest.of(0, 10, Direction.DESC, "priority");
            TodoListReq req = new TodoListReq(null, null, null);

            // when
            Page<TodoRes> list = todoService.todoList(user.getId(), pageRequest, req);

            // then
            // 우선순위 내림차순 정렬, 기본 ID 내림차순 정렬 적용
            assertThat(list.getContent().get(0).priority()).isEqualTo(TodosPriority.HIGH);
        }

        @Test
        public void 검색_제목() {
            // give
            PageRequest pageRequest = PageRequest.of(0, 10, Direction.DESC, "priority");
            TodoListReq req = new TodoListReq("2", "title", null);

            // when
            Page<TodoRes> list = todoService.todoList(user.getId(), pageRequest, req);

            // then
            assertThat(list).hasSize(2);
            assertThat(list.getContent().stream()
                    .allMatch(todo -> todo.title().contains("2"))).isTrue();
        }

        @Test
        public void 검색_설명() {
            // give
            PageRequest pageRequest = PageRequest.of(0, 10, Direction.DESC, "priority");
            TodoListReq req = new TodoListReq("2", "description", null);

            // when
            Page<TodoRes> list = todoService.todoList(user.getId(), pageRequest, req);

            // then
            assertThat(list).hasSize(2);
            assertThat(list.getContent().stream().allMatch(
                    todo -> todo.description().contains("2"))
            ).isTrue();
        }

        @Test
        public void 검색_제목_설명() {
            // give
            PageRequest pageRequest = PageRequest.of(0, 10, Direction.DESC, "priority");
            TodoListReq req = new TodoListReq("2", "description,title", null);

            // when
            Page<TodoRes> list = todoService.todoList(user.getId(), pageRequest, req);

            // then
            assertThat(list).hasSize(4);
            assertThat(list.getContent().stream().allMatch(
                    todo -> todo.title().contains("2") || todo.description().contains("2"))
            ).isTrue();
        }

        @Test
        public void 검색_상태() {
            // give
            PageRequest pageRequest = PageRequest.of(0, 10, Direction.DESC, "priority");
            TodoListReq req = new TodoListReq(null, null, TodosStatus.PENDING);

            // when
            Page<TodoRes> list = todoService.todoList(user.getId(), pageRequest, req);

            // then
            assertThat(list).hasSize(6);
            assertThat(list.getContent().stream().allMatch(
                    todo -> todo.status().equals(TodosStatus.PENDING))
            ).isTrue();
        }
    }
}