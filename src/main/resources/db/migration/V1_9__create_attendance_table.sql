CREATE TABLE attendance (
    id int PRIMARY KEY AUTO_INCREMENT,
    student_id int NOT NULL REFERENCES student(id) ON DELETE CASCADE,
    date DATE NOT NULL,
    status ENUM('ABSENT', 'PRESENT', 'LATE') NOT NULL,
    teacher_id int REFERENCES teacher(id)
);
