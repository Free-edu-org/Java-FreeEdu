CREATE TABLE parent (
    id int PRIMARY KEY REFERENCES user(id),
    contact_info VARCHAR(255)
);
