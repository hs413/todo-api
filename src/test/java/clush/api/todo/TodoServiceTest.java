package clush.api.todo;

import clush.api.account.entity.Users;
import clush.api.auth.UserInfo;
import clush.api.todo.entity.Todos;
import clush.api.todo.entity.request.TodoCreateReq;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
        UserInfo userInfo = new UserInfo(user.getId(), user.getUsername());
        TodoCreateReq req = new TodoCreateReq("todo 1", "todo todo",
                null, null, null);

        // when
        Long todoId = todoService.todoCreate(userInfo, req);

        // then
        Todos todos = em.find(Todos.class, todoId);
        Assertions.assertThat(todos.getTitle()).isEqualTo("todo 1");
    }
}