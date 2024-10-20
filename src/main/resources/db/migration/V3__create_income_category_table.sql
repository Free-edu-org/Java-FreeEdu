CREATE TABLE income_category
(
    id   int          NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    FOREIGN KEY (id) REFERENCES "user" (id)
);
