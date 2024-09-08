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
import todo.api.notifications.entity.request.NotificationUpdateReq;
import todo.api.notifications.entity.response.NotificationRes;
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
                .username("유저")
                .email("email1@example.com")
                .password("1234")
                .build();

        todo = Todos.builder()
                .title("todo 제목")
                .description("todo 설명")
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
        LocalDateTime now = LocalDateTime.now().plusMinutes(10);
        NotificationCreateReq req = new NotificationCreateReq(now, "할일 확인!",
                todo.getId(), 0, null, null);

        // when
        Long notificationId = notificationService.notificationCreate(user.getId(), req);

        // then
        Notifications notification = em.find(Notifications.class, notificationId);
        assertThat(notification.getRepeatCount()).isEqualTo(0);
        assertThat(notification.getMessage()).isEqualTo("할일 확인!");
        assertThat(notification.getDueDate()).isEqualTo(now.withSecond(0).withNano(0));
        assertThat(notification.getVersion()).isEqualTo(0);
        notificationService.sendNotification(notificationId);
    }

    @Test
    public void 알림_조회() {
        // give
        Notifications notification = Notifications.builder()
                .todo(todo)
                .user(user)
                .dueDate(LocalDateTime.now().plusMinutes(10))
                .message("할일 확인!")
                .build();

        em.persist(notification);
        em.flush();
        em.clear();

        // when
        NotificationRes res = notificationService
                .notificationDetail(user.getId(), notification.getId());

        // then
        assertThat(res.repeatCount()).isEqualTo(0);
        assertThat(res.message()).isEqualTo("할일 확인!");
    }


    @Test
    public void 알림_수정() {
        LocalDateTime now = LocalDateTime.now().plusMinutes(10);
        // give
        Notifications notification = Notifications.builder()
                .todo(todo)
                .user(user)
                .dueDate(now)
                .message("할일 확인!")
                .build();

        em.persist(notification);
        em.flush();
        em.clear();

        NotificationUpdateReq req = new NotificationUpdateReq(now, "할일 확인!!",
                null, null, null);

        // when
        Long updatedId = notificationService
                .notificationUpdate(user.getId(), notification.getId(), req);
        em.flush();
        em.clear();

        // then
        Notifications updated = em.find(Notifications.class, updatedId);
        assertThat(updated.getRepeatCount()).isEqualTo(0);
        assertThat(updated.getMessage()).isEqualTo("할일 확인!!");
        assertThat(updated.getVersion()).isEqualTo(1);
    }

    @Test
    public void 알림_삭제() {
        LocalDateTime now = LocalDateTime.now().plusMinutes(10);
        // give
        Notifications notification = Notifications.builder()
                .todo(todo)
                .user(user)
                .dueDate(now)
                .message("할일 확인!")
                .build();

        em.persist(notification);
        em.flush();
        em.clear();

        NotificationUpdateReq req = new NotificationUpdateReq(now, "할일 확인!!",
                null, null, null);

        // when
        notificationService.notificationDelete(user.getId(), notification.getId());
        em.flush();
        em.clear();

        // then
        Notifications deleted = em.find(Notifications.class, notification.getId());
        assertThat(deleted).isNull();
    }
}