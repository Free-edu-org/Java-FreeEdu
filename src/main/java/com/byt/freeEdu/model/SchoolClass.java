package com.byt.freeEdu.model;

import java.util.List;

import com.byt.freeEdu.model.DTO.StudentDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@Entity
@RequiredArgsConstructor
@NoArgsConstructor
@Table(name = "class")
public class SchoolClass{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", unique = true, updatable = false, nullable = false)
  private int schoolClassId;

  @NonNull
  @Column(name = "name", nullable = false, unique = true)
  private String name;

  @Transient
  private long studentCount;

  @Transient
  private List<StudentDto> students;
}
