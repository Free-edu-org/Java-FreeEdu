package com.byt.freeEdu.model.DTO;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ScheduleAdminDto{

  private int id;

  private LocalDate date;

  private String subjectName;

  private String className;

  private int teacherId;

  private String teacherFirstName;

  private String teacherLastName;
}
