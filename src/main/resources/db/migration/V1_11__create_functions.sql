DELIMITER //

CREATE PROCEDURE addUserToAdmins(IN userId INT)
BEGIN
    DELETE FROM admin WHERE id = userId;
    DELETE FROM student WHERE id = userId;
    DELETE FROM parent WHERE id = userId;
    INSERT INTO FreeEduDB.admin(id) VALUES (userId);
END //

DELIMITER ;

DELIMITER //

CREATE PROCEDURE addUserToStudents(IN userId INT, IN classId INT, IN parentId INT)
BEGIN
    DELETE FROM admin WHERE id = userId;
    DELETE FROM parent WHERE id = userId;
    DELETE FROM teacher WHERE id = userId;
    INSERT INTO FreeEduDB.student (id, class_id, parent_id) VALUES (userId, classId, parentId);
END //

DELIMITER ;

DELIMITER //

CREATE PROCEDURE addUserToTeachers(IN userId INT)
BEGIN
    DELETE FROM admin WHERE id = userId;
    DELETE FROM student WHERE id = userId;
    DELETE FROM parent WHERE id = userId;
    INSERT INTO FreeEduDB.teacher (id) VALUES (userId);
END //

DELIMITER ;

DELIMITER //

CREATE PROCEDURE addUserToParents(IN userId INT, IN contactInfo VARCHAR(255))
BEGIN
    DELETE FROM admin WHERE id = userId;
    DELETE FROM student WHERE id = userId;
    DELETE FROM teacher WHERE id = userId;
    INSERT INTO FreeEduDB.parent (id, contact_info) VALUES (userId, contactInfo);
END //

DELIMITER ;
