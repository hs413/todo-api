package todo.api.todo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import todo.api.account.entity.Users;
import todo.api.common.exception.CustomException;
import todo.api.todo.TodoErrorCode;
import todo.api.todo.entity.Todos;
import todo.api.todo.entity.TodosSharing;
import todo.api.todo.entity.enums.TodosPriority;
import todo.api.todo.entity.enums.TodosStatus;
import todo.api.todo.entity.request.SharedReq;
import todo.api.todo.entity.request.TodoListReq;
import todo.api.todo.entity.response.TodoRes;

@SpringBootTest
@Log4j2
@Transactional
class SharedServiceTest {

    @Autowired
    private SharedService sharedService;

    @Autowired
    private TodoService todoService;

    @PersistenceContext
    private EntityManager em;

    Users user1;
    Users user2;

    Todos user1Todo1;
    Todos user1Todo2;
    Todos user2todo1;

    @BeforeEach
    void setUp() {
        user1 = Users.builder()
                .username("유저1")
                .email("email1@example.com")
                .password("1234")
                .build();

        user2 = Users.builder()
                .username("유저2")
                .email("email2@example.com")
                .password("1234")
                .build();

        user1Todo1 = Todos.builder()
                .title("user1 todo1")
                .description("user1 todo1")
                .status(TodosStatus.PENDING)
                .priority(TodosPriority.MID)
                .user(user1)
                .build();

        user1Todo2 = Todos.builder()
                .title("user1 todo2")
                .description("user1 todo2")
                .status(TodosStatus.PENDING)
                .priority(TodosPriority.MID)
                .user(user1)
                .build();

        user2todo1 = Todos.builder()
                .title("user2 todo1")
                .description("user2 todo1")
                .status(TodosStatus.PENDING)
                .priority(TodosPriority.MID)
                .user(user2)
                .build();

        em.persist(user1);
        em.persist(user2);
        em.persist(user1Todo1);
        em.persist(user1Todo2);
        em.persist(user2todo1);
        em.flush();
        em.clear();
    }

    @Test
    public void 할일_공유() {
        // give
        SharedReq req = new SharedReq(user2.getEmail(), null);

        // when
        Long sharedId = sharedService.todoShare(user1.getId(), user1Todo1.getId(), req);

        // then
        TodosSharing shared = em.find(TodosSharing.class, sharedId);
        assertThat(shared.getTodos().getId()).isEqualTo(user1Todo1.getId());
    }

    @Test
    public void 할일_공유_중복() {
        // give
        SharedReq req = new SharedReq(user2.getEmail(), null);

        // when
        sharedService.todoShare(user1.getId(), user1Todo1.getId(), req);

        CustomException ex = assertThrows(CustomException.class,
                () -> sharedService.todoShare(user1.getId(), user1Todo1.getId(), req));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(TodoErrorCode.ALREADY_SHARED);
    }

    @Test
    public void 공유된_할일_상세_조회() {
        // give
        SharedReq req = new SharedReq(user2.getEmail(), null);
        sharedService.todoShare(user1.getId(), user1Todo1.getId(), req);

        // when
        TodoRes res = todoService.todoDetail(user2.getId(), user1Todo1.getId());

        assertThat(res.id()).isEqualTo(user1Todo1.getId());
    }

    @Test
    public void 공유되지않은_할일_상세_조회() {
        // when
        CustomException ex = assertThrows(CustomException.class,
                () -> todoService.todoDetail(user2.getId(), user1Todo1.getId()));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(TodoErrorCode.NO_TODOS);
    }


    @Nested
    class SharedListTest {

        @BeforeEach
        public void setup() {
//            for (int i = 1; i <= 10; i++) {
//                Todos todo = Todos.builder()
//                        .title("todo " + i)
//                        .description("todo todo " + (13 - i))
//                        .status(i % 2 == 1 ? TodosStatus.PENDING : TodosStatus.IN_PROGRESS)
//                        .priority(TodosPriority.fromValue(i % 3 + 1))
//                        .user(user1)
//                        .build();
//                em.persist(todo);
//            }
//            em.flush();
//            em.clear();
        }


        @Test
        public void 공유된_할일_목록() {
            SharedReq sharedReq = new SharedReq(user2.getEmail(), null);
            sharedService.todoShare(user1.getId(), user1Todo1.getId(), sharedReq);
            sharedService.todoShare(user1.getId(), user1Todo2.getId(), sharedReq);

            PageRequest pageRequest = PageRequest.of(0, 10);
            TodoListReq req = new TodoListReq(null, null, null);
            Page<TodoRes> res = sharedService.sharedList(user2.getId(), pageRequest, req);

            assertThat(res.getContent()).hasSize(2);
        }
    }
}