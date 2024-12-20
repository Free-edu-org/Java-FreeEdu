CREATE TABLE remark (
    id int PRIMARY KEY AUTO_INCREMENT,
    student_id int NOT NULL REFERENCES student(id) ON DELETE CASCADE,
    teacher_id int NOT NULL REFERENCES teacher(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    add_date DATE NOT NULL
);
