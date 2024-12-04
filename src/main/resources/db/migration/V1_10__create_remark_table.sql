CREATE TABLE remark (
    id int PRIMARY KEY,
    student_id int NOT NULL REFERENCES student(id),
    teacher_id int NOT NULL REFERENCES teacher(id),
    content TEXT NOT NULL,
    add_date DATE NOT NULL
);
