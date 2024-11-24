CREATE TABLE test
(
    id        int          NOT NULL PRIMARY KEY,
    username  VARCHAR(255) NOT NULL,
    firstname VARCHAR(255),
    surname   VARCHAR(255),
    email     VARCHAR(255) NOT NULL UNIQUE
);
