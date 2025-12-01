CREATE TABLE events (
                        id BIGSERIAL PRIMARY KEY,
                        title VARCHAR(255) NOT NULL,
                        topic VARCHAR(255),
                        content TEXT,
                        due_date TIMESTAMP NOT NULL,
                        user_id BIGINT,

                        CONSTRAINT fk_events_user
                            FOREIGN KEY (user_id)
                                REFERENCES usersecurity(id)
                                ON DELETE SET NULL
);
