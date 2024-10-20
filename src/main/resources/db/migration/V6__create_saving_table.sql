CREATE TABLE saving
(
    id         int            NOT NULL PRIMARY KEY,
    date_start DATE           NOT NULL,
    date_end   DATE,
    amount     DECIMAL(10, 2) NOT NULL,
    name       VARCHAR(255)   NOT NULL,
    FOREIGN KEY (id) REFERENCES "user" (id)
);
