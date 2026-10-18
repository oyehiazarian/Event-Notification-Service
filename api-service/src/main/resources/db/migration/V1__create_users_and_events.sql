CREATE TABLE IF NOT EXISTS usersecurity (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    vorname VARCHAR(255) NOT NULL,
    nachname VARCHAR(255) NOT NULL,
    telefonnummer VARCHAR(255),
    role VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS events (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    topic VARCHAR(255),
    content TEXT,
    due_date TIMESTAMP NOT NULL,
    user_id BIGINT,
    CONSTRAINT fk_events_user FOREIGN KEY (user_id) REFERENCES usersecurity (id) ON DELETE SET NULL
);
