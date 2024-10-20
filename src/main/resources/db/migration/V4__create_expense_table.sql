CREATE TABLE expense
(
    id          int            NOT NULL PRIMARY KEY,
    date        DATE           NOT NULL,
    amount      DECIMAL(10, 2) NOT NULL,
    currency    VARCHAR(10)    NOT NULL,
    description VARCHAR(255),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id) REFERENCES expense_category (id),
    FOREIGN KEY (id) REFERENCES "user" (id)
);

