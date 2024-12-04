CREATE TABLE admin (
    id int PRIMARY KEY REFERENCES user(id)
);
