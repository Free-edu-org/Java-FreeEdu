CREATE TABLE parent (
    id int PRIMARY KEY REFERENCES user(id) ON DELETE CASCADE,
    contact_info VARCHAR(255)
);
