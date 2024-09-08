CREATE TABLE notifications
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT       NOT NULL,
    todo_id         BIGINT       NOT NULL,
    due_date        TIMESTAMP    NOT NULL,
    message         VARCHAR(255) NOT NULL,
    repeat_count    TINYINT   DEFAULT 0,
    repeat_interval INT,
    repeat_unit     VARCHAR(10),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE (user_id, todo_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (todo_id) REFERENCES todos (id) ON DELETE CASCADE
);
