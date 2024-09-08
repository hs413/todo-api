package todo.api.notifications.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import todo.api.account.entity.Users;
import todo.api.common.BaseEntity;
import todo.api.todo.entity.Todos;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "todo_id"})})
public class Notifications extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dueDate;

    @Column(nullable = false)
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id")
    private Todos todo;

    @Column(nullable = false)
    private Integer repeatCount;

    private Integer repeatInterval;

    private RepeatUnit repeatUnit;

    @PrePersist
    @PreUpdate
    public void prePersistUpdate() {
        dueDate = dueDate.withSecond(0).withNano(0);  // 초를 0으로 설정
        repeatCount = repeatCount == null ? 0 : repeatCount;

        if (repeatCount > 0) {
            repeatInterval = repeatInterval != null ? repeatInterval : 1;
        }

        if (repeatCount > 0) {
            repeatUnit = repeatUnit != null ? repeatUnit : RepeatUnit.HOUR;
        }
    }

}
