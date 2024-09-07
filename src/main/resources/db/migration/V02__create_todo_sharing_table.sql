CREATE TABLE todos_sharing
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    shared_user_id BIGINT NOT NULL,
    todo_id        BIGINT NOT NULL,
    permission     VARCHAR(10) DEFAULT 'READ_ONLY',
    UNIQUE (shared_user_id, todo_id),
    FOREIGN KEY (todo_id) REFERENCES todos (id) ON DELETE CASCADE,
    FOREIGN KEY (shared_user_id) REFERENCES users (id) ON DELETE CASCADE
);
