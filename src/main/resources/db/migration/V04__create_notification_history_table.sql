CREATE TABLE notifications_history
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    notification_id BIGINT NOT NULL,
    version         INT       DEFAULT 1,
    sent_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (notification_id) REFERENCES notifications (id) ON DELETE CASCADE
);

ALTER TABLE notifications
    ADD COLUMN version INT DEFAULT 1;