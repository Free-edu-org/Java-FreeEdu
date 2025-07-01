package com.byt.freeEdu.model;

import java.time.LocalDate;

import com.byt.freeEdu.model.users.Student;
import com.byt.freeEdu.model.users.Teacher;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "remark")
public class Remark{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false, unique = true, nullable = false)
  private int remarkId;

  @NonNull
  @Column(name = "content", nullable = false)
  private String content;

  @NonNull
  @Column(name = "add_date", nullable = false)
  private LocalDate addDate;

  @NonNull
  @ManyToOne
  @JoinColumn(name = "student_id", nullable = false)
  private Student student;

  @NonNull
  @ManyToOne
  @JoinColumn(name = "teacher_id", nullable = false)
  private Teacher teacher;
}
