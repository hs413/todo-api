CREATE TABLE notifications
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT    NOT NULL,
    todo_id         BIGINT,
    due_date        TIMESTAMP NOT NULL,
    repeat_count    TINYINT   DEFAULT 0,
    repeat_interval INT,
    repeat_unit     VARCHAR(10),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (todo_id) REFERENCES todos (id) ON DELETE SET NULL
);
