package clush.api.todo.entity;

import static org.springframework.util.StringUtils.hasText;

import clush.api.account.entity.Users;
import clush.api.common.BaseEntity;
import clush.api.todo.entity.request.TodoUpdateReq;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Todos extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TodosStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TodosPriority priority;

    @Column(nullable = true)
    private LocalDateTime dueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;


    public void update(TodoUpdateReq req) {
        if (hasText(req.title())) {
            title = req.title();
        }

        if (hasText(req.description())) {
            description = req.description();
        }

        if (req.status() != null) {
            status = req.status();
        }

        if (req.priority() != null) {
            priority = req.priority();
        }

        if (req.dueDate() != null) {
            dueDate = req.dueDate();
        }
    }
}
