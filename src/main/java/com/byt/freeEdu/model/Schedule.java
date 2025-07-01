package com.byt.freeEdu.model;

import java.time.LocalDate;

import com.byt.freeEdu.model.enums.SubjectEnum;
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
@Table(name = "schedule")
public class Schedule{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false, unique = true, nullable = false)
  private int scheduleId;

  @NonNull
  @Column(name = "date", nullable = false)
  private LocalDate date;

  @NonNull
  @Enumerated(EnumType.STRING)
  @Column(name = "subject", nullable = false)
  private SubjectEnum subject;

  @NonNull
  @ManyToOne
  @JoinColumn(name = "class_id", nullable = false)
  private SchoolClass schoolClass;

  @NonNull
  @ManyToOne
  @JoinColumn(name = "teacher_id", nullable = false)
  private Teacher teacher;
}
