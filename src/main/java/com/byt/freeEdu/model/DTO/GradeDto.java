package com.byt.freeEdu.model.DTO;

import com.byt.freeEdu.model.enums.SubjectEnum;
import lombok.Data;

import java.time.LocalDate;

@Data
public class GradeDto {

    private int gradeId;
    private String subject;
    private SubjectEnum subjectEnum;
    private double value;

    private int studentId;
    private String studentFirstName;
    private String studentLastName;

    private int teacherId;
    private String teacherFirstName;
    private String teacherLastName;

    private LocalDate gradeDate;
}
