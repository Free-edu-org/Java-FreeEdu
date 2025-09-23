CREATE TABLE student (
    id int PRIMARY KEY REFERENCES user(id) ON DELETE CASCADE,
    class_id int NOT NULL REFERENCES class(id),
    parent_id int REFERENCES parent(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
