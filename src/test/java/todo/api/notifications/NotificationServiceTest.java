package todo.api.notifications;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import todo.api.account.entity.Users;
import todo.api.notifications.entity.Notifications;
import todo.api.notifications.entity.request.NotificationCreateReq;
import todo.api.todo.entity.Todos;
import todo.api.todo.entity.enums.TodosPriority;
import todo.api.todo.entity.enums.TodosStatus;

@SpringBootTest
@Log4j2
@Transactional
class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    @PersistenceContext
    private EntityManager em;

    Users user;
    Todos todo;

    @BeforeEach
    void setUp() {
        user = Users.builder()
                .username("유저1")
                .email("email1@example.com")
                .password("1234")
                .build();

        todo = Todos.builder()
                .title("user2 todo1")
                .description("user2 todo1")
                .status(TodosStatus.PENDING)
                .priority(TodosPriority.MID)
                .user(user)
                .build();

        em.persist(user);
        em.persist(todo);
        em.flush();
        em.clear();
    }


    @Test
    public void 알림_등록() {
        // give
        LocalDateTime now = LocalDateTime.now();
        NotificationCreateReq req = new NotificationCreateReq(now.plusMinutes(10),
                todo.getId(), 0, null, null);

        // when
        Long notificationId = notificationService.notificationCreate(user.getId(), req);

        // then
        Notifications notification = em.find(Notifications.class, notificationId);
        assertThat(notification.getRepeatCount()).isEqualTo(0);
        assertThat(notification.getDueDate()).isEqualTo(now.withSecond(0).withNano(0));
    }
}