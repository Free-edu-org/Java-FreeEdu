package com.byt.freeEdu.model.DTO;

import java.time.LocalDate;

import com.byt.freeEdu.model.enums.SubjectEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GradeDto{

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
