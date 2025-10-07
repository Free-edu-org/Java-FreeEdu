CREATE TABLE schedule (
    id int PRIMARY KEY AUTO_INCREMENT,
    occurrence_datetime DATETIME NOT NULL,
    subject_id int NOT NULL REFERENCES subject(id),
    teacher_id int NOT NULL REFERENCES teacher(id),
    class_id int NOT NULL REFERENCES class(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
