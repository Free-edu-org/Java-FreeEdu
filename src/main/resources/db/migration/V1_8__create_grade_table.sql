CREATE TABLE grade (
    id int PRIMARY KEY,
    value DECIMAL(3, 2) NOT NULL CHECK (value >= 0 AND value <= 6),
    student_id int NOT NULL REFERENCES student(id),
    teacher_id int NOT NULL REFERENCES teacher(id),
    subject ENUM('Polish', 'Math', 'Geography') NOT NULL,
    add_date DATE NOT NULL
);
