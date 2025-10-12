package com.byt.freeEdu.model;

import java.time.LocalDate;

import com.byt.freeEdu.model.enums.SubjectEnum;
import com.byt.freeEdu.model.users.Student;
import com.byt.freeEdu.model.users.Teacher;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "grade")
public class Grade{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", unique = true, updatable = false, nullable = false)
  private int gradeId;

  @NonNull
  @Enumerated(EnumType.STRING)
  @Column(name = "subject", nullable = false)
  private SubjectEnum subject;

  @NonNull
  @Column(name = "value", nullable = false)
  private double value;

  @NonNull
  @ManyToOne
  @JoinColumn(name = "student_id", nullable = false)
  private Student student;

  @NonNull
  @ManyToOne
  @JoinColumn(name = "teacher_id", nullable = false)
  private Teacher teacher;

  @NonNull
  @Column(name = "add_date", nullable = false)
  private LocalDate gradeDate;
}
