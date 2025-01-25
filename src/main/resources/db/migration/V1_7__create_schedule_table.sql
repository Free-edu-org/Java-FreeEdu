CREATE TABLE schedule (
    id int PRIMARY KEY AUTO_INCREMENT,
    date DATE NOT NULL,
    subject ENUM('POLISH', 'MATH', 'GEOGRAPHY', 'SCIENCE', 'ART', 'HISTORY', 'SPORTS') NOT NULL,
    teacher_id int REFERENCES teacher(id),
    class_id int NOT NULL REFERENCES class(id)
);
