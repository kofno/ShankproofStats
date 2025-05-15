CREATE TABLE task (
                      task_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      description VARCHAR(255) NOT NULL,
                      creation_date TIMESTAMP NOT NULL,
                      due_date DATE,
                      completed BOOLEAN NOT NULL DEFAULT FALSE
);