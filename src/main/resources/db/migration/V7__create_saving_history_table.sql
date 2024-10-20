CREATE TABLE saving_history
(
    id         int            NOT NULL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    amount     DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (id) REFERENCES saving (id),
    FOREIGN KEY (id) REFERENCES expense (id),
    FOREIGN KEY (id) REFERENCES income (id),
    FOREIGN KEY (id) REFERENCES "user" (id)
);
