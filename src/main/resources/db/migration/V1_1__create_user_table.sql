CREATE TABLE user (
    id int PRIMARY KEY AUTO_INCREMENT,
    firstname VARCHAR(50) NOT NULL,
    lastname VARCHAR(50) NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    user_role ENUM('Parent', 'Teacher', 'Student', 'Admin') NOT NULL
);
