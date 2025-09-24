package com.byt.freeEdu.model.DTO;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ScheduleDto{

  private Integer id;

  private LocalDate date;

  private String subjectName;

  private String className;

  private Integer teacherId;

  private String teacherFirstName;

  private String teacherLastName;
}
