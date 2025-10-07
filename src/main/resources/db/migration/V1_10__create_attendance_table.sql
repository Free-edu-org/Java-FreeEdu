CREATE TABLE attendance (
    id int PRIMARY KEY AUTO_INCREMENT,
    status ENUM('ABSENT', 'PRESENT', 'LATE') NOT NULL,
    student_id int NOT NULL REFERENCES student(id) ON DELETE CASCADE,
    schedule_id int NOT NULL REFERENCES schedule(id) ON DELETE CASCADE,
    subject_id int NOT NULL REFERENCES subject(id) ON DELETE CASCADE,
    teacher_id int REFERENCES teacher(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
