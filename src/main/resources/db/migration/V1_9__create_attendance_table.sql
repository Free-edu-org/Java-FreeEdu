CREATE TABLE attendance (
    id int PRIMARY KEY,
    student_id int NOT NULL REFERENCES student(id),
    date DATE NOT NULL,
    status ENUM('absent', 'present', 'late') NOT NULL,
    teacher_id int REFERENCES teacher(id)
);
