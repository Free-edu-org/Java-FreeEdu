CREATE TABLE schedule (
    id int PRIMARY KEY AUTO_INCREMENT,
    date DATE NOT NULL,
    subject ENUM('Polish', 'Math', 'Geography') NOT NULL,
    teacher_id int REFERENCES teacher(id),
    class_id int NOT NULL REFERENCES class(id)
);
