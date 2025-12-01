CREATE TABLE usersecurity (
                              id BIGSERIAL PRIMARY KEY,
                              username VARCHAR(255) NOT NULL UNIQUE,
                              password VARCHAR(255) NOT NULL,
                              vorname VARCHAR(255) NOT NULL,
                              nachname VARCHAR(255) NOT NULL,
                              telefonnummer VARCHAR(255),
                              role VARCHAR(255) NOT NULL,
                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
